package com.rahul.fieldflow.core.audio

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.rahul.fieldflow.domain.usecase.recording.CreateRecordingSessionUseCase
import com.rahul.fieldflow.domain.usecase.recording.TriggerTranscriptionUseCase
import com.rahul.fieldflow.domain.usecase.recording.UpdateRecordingStatusUseCase
import com.rahul.fieldflow.domain.usecase.recording.UploadRecordingUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.io.File
import java.time.OffsetDateTime
import java.time.Duration
import javax.inject.Inject

@AndroidEntryPoint
class RecordingService : Service() {

    @Inject lateinit var audioRecorder: AudioRecorder
    @Inject lateinit var createSessionUseCase: CreateRecordingSessionUseCase
    @Inject lateinit var updateStatusUseCase: UpdateRecordingStatusUseCase
    @Inject lateinit var uploadUseCase: UploadRecordingUseCase
    @Inject lateinit var triggerTranscriptionUseCase: TriggerTranscriptionUseCase
    @Inject lateinit var recordingManager: RecordingManager

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var audioManager: AudioManager
    
    private var currentTaskId: String? = null
    private var currentSessionId: String? = null
    private var currentFile: File? = null
    private var startedAt: OffsetDateTime? = null
    private var isInterrupted = false

    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                Log.d("RecordingService", "Audio focus lost, interrupting recording")
                handleInterruption()
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                Log.d("RecordingService", "Audio focus gained, attempting to resume")
                if (isInterrupted) {
                    resumeRecording()
                }
            }
        }
    }

    override fun onCreate() {
        Log.d("RECORD_DEBUG", "RECORDING_SERVICE_ON_CREATE")
        Log.d("RecordingService", "onCreate")
        super.onCreate()
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        val taskId = intent?.getStringExtra(EXTRA_TASK_ID)
        Log.d("RECORD_DEBUG", "RECORDING_SERVICE_ON_START_COMMAND action=$action taskId=$taskId")
        Log.d("RecordingService", "onStartCommand action=$action taskId=$taskId")
        
        if (intent == null) {
            Log.w("RecordingService", "Service restarted with null intent")
            return START_NOT_STICKY
        }

        when (action) {
            ACTION_START -> {
                if (taskId == null) {
                    Log.e("RecordingService", "ACTION_START without taskId")
                    return START_NOT_STICKY
                }
                
                // PRODUCTION HARDENING: Avoid restarting if already recording the same task
                if (currentTaskId == taskId && audioRecorder.isRecording) {
                    Log.d("RecordingService", "Already recording task $taskId, ignoring start action")
                } else {
                    // Force reset if we were in a failed/null state for this service instance
                    startNewRecording(taskId)
                }
            }
            ACTION_STOP -> {
                finalizeAndStop()
            }
        }
        return START_STICKY
    }

    private fun startNewRecording(taskId: String) {
        Log.d("RecordingService", "startNewRecording taskId=$taskId")
        currentTaskId = taskId
        isInterrupted = false
        
        val micPermission = androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) == android.content.pm.PackageManager.PERMISSION_GRANTED
        Log.d("RECORD_DEBUG", "MIC_PERMISSION_GRANTED=$micPermission")
        if (!micPermission) {
            Log.e("RECORD_DEBUG", "MIC_PERMISSION_DENIED")
            recordingManager.updateState(status = RecordingStatus.FAILED, error = "Microphone permission denied")
            stopSelf()
            return
        }

        val focusGranted = requestAudioFocus()
        Log.d("RECORD_DEBUG", "AUDIO_FOCUS_GRANTED=$focusGranted")
        Log.d("RecordingService", "Audio focus granted: $focusGranted")
        
        if (focusGranted) {
            Log.d("RECORD_DEBUG", "CALLING_START_FOREGROUND")
            showRecordingNotification("Recording in progress...")
            Log.d("RECORD_DEBUG", "START_FOREGROUND_SUCCESS")
            
            serviceScope.launch {
                Log.d("RECORD_DEBUG", "CREATING_RECORDING_SESSION taskId=$taskId")
                Log.d("RecordingService", "Calling createSessionUseCase...")
                createSessionUseCase(taskId).onSuccess { session ->
                    Log.d("RECORD_DEBUG", "RECORDING_SESSION_CREATED sessionId=${session.id}")
                    Log.d("RecordingService", "Session created successfully id=${session.id}")
                    currentSessionId = session.id
                    startedAt = session.startedAt
                    
                    recordingManager.updateState(
                        status = RecordingStatus.RECORDING,
                        taskId = taskId,
                        sessionId = session.id,
                        startedAt = session.startedAt
                    )
                    
                    val file = File(cacheDir, "task_${taskId}_session_${session.id}.m4a")
                    currentFile = file
                    Log.d("RecordingService", "Starting audioRecorder with file=${file.absolutePath}")
                    audioRecorder.start(file)
                }.onFailure { e ->
                    Log.e("RECORD_DEBUG", "RECORDING_SESSION_CREATE_FAILED exception=${e.javaClass.simpleName} message=${e.message}")
                    Log.e("RecordingService", "Failed to create session in UseCase", e)
                    recordingManager.updateState(
                        status = RecordingStatus.FAILED,
                        error = e.message
                    )
                    currentTaskId = null // RESET on failure to allow retry
                    stopSelf()
                }
            }
        } else {
            Log.e("RECORD_DEBUG", "AUDIO_FOCUS_FAILED")
            Log.e("RecordingService", "Could not gain audio focus")
            recordingManager.updateState(
                status = RecordingStatus.FAILED,
                error = "Could not gain audio focus"
            )
            currentTaskId = null // RESET on failure
            stopSelf()
        }
    }

    private fun handleInterruption() {
        if (!audioRecorder.isRecording) return
        
        isInterrupted = true
        audioRecorder.stop()
        
        val taskId = currentTaskId ?: return
        val sessionId = currentSessionId ?: return
        val file = currentFile ?: return
        val end = OffsetDateTime.now()
        val duration = Duration.between(startedAt, end).seconds.toInt()

        recordingManager.updateState(status = RecordingStatus.INTERRUPTED)
        showRecordingNotification("Recording interrupted. Will resume shortly.")

        serviceScope.launch {
            // Update current session as interrupted
            Log.d("RecordingService", "Handling interruption: sessionId=$sessionId")
            updateStatusUseCase(sessionId, "interrupted", end, duration)
            
            Log.d("RECORD_DEBUG", "RECORDING_UPLOAD_START (interrupted) sessionId=$sessionId")
            uploadUseCase(taskId, sessionId, file).onSuccess {
                Log.d("RECORD_DEBUG", "RECORDING_UPLOAD_SUCCESS (interrupted) sessionId=$sessionId")
                
                Log.d("RECORD_DEBUG", "TRANSCRIPTION_TRIGGER_START (interrupted) sessionId=$sessionId")
                triggerTranscriptionUseCase(sessionId).onSuccess {
                    Log.d("RECORD_DEBUG", "TRANSCRIPTION_TRIGGER_SUCCESS (interrupted) sessionId=$sessionId")
                }.onFailure { e ->
                    Log.e("RECORD_DEBUG", "TRANSCRIPTION_TRIGGER_FAILED (interrupted) sessionId=$sessionId error=${e.message}")
                }
            }.onFailure { e ->
                Log.e("RECORD_DEBUG", "RECORDING_UPLOAD_FAILED (interrupted) sessionId=$sessionId error=${e.message}")
            }
        }
    }

    private fun resumeRecording() {
        val taskId = currentTaskId ?: return
        isInterrupted = false
        
        Log.d("RecordingService", "Resuming recording with new session")
        startNewRecording(taskId)
    }

    private fun finalizeAndStop() {
        audioRecorder.stop()
        abandonAudioFocus()
        
        val taskId = currentTaskId ?: return
        val sessionId = currentSessionId ?: return
        val file = currentFile ?: return
        val end = OffsetDateTime.now()
        val duration = Duration.between(startedAt, end).seconds.toInt()

        recordingManager.updateState(status = RecordingStatus.COMPLETED)

        serviceScope.launch {
            Log.d("RecordingService", "Finalizing recording: sessionId=$sessionId")
            updateStatusUseCase(sessionId, "completed", end, duration)
            
            Log.d("RECORD_DEBUG", "RECORDING_UPLOAD_START sessionId=$sessionId")
            uploadUseCase(taskId, sessionId, file).onSuccess {
                Log.d("RECORD_DEBUG", "RECORDING_UPLOAD_SUCCESS sessionId=$sessionId")
                
                Log.d("RECORD_DEBUG", "TRANSCRIPTION_TRIGGER_START sessionId=$sessionId")
                triggerTranscriptionUseCase(sessionId).onSuccess {
                    Log.d("RECORD_DEBUG", "TRANSCRIPTION_TRIGGER_SUCCESS sessionId=$sessionId")
                }.onFailure { e ->
                    Log.e("RECORD_DEBUG", "TRANSCRIPTION_TRIGGER_FAILED sessionId=$sessionId error=${e.message}")
                }
            }.onFailure { e ->
                Log.e("RECORD_DEBUG", "RECORDING_UPLOAD_FAILED sessionId=$sessionId error=${e.message}")
            }
            
            withContext(Dispatchers.Main) {
                stopForeground(STOP_FOREGROUND_REMOVE)
                recordingManager.reset()
                stopSelf()
            }
        }
    }

    private fun requestAudioFocus(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val playbackAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
            val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE)
                .setAudioAttributes(playbackAttributes)
                .setAcceptsDelayedFocusGain(true)
                .setOnAudioFocusChangeListener(audioFocusChangeListener)
                .build()
            audioManager.requestAudioFocus(focusRequest) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(
                audioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE
            ) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        }
    }

    private fun abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // In a real app we'd need to keep the focusRequest object
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(audioFocusChangeListener)
        }
    }

    private fun showRecordingNotification(content: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificationPermission = androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED
            Log.d("RECORD_DEBUG", "NOTIFICATION_PERMISSION_GRANTED=$notificationPermission")
        }

        val channelId = "recording_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Task Recording", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("FieldFlow Recording")
            .setContentText(content)
            .setSmallIcon(android.R.drawable.presence_audio_online)
            .setOngoing(true)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        recordingManager.reset()
        audioRecorder.release()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val NOTIFICATION_ID = 1001
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val EXTRA_TASK_ID = "EXTRA_TASK_ID"
    }
}
