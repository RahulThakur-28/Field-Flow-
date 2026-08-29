package com.rahul.fieldflow.core.audio

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class AudioRecorder @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var recorder: MediaRecorder? = null
    private var _isRecording = false
    val isRecording: Boolean get() = _isRecording

    fun start(outputFile: File) {
        Log.d("RECORD_DEBUG", "AUDIO_RECORDER_INITIALIZING")
        Log.d("AudioRecorder", "start called with file=${outputFile.absolutePath}")
        try {
            if (outputFile.exists()) {
                Log.d("AudioRecorder", "Deleting existing file")
                outputFile.delete()
            }
            Log.d("RECORD_DEBUG", "AUDIO_RECORDER_OUTPUT_FILE_CREATED")
            recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(FileOutputStream(outputFile).fd)

                Log.d("RECORD_DEBUG", "MEDIA_RECORDER_PREPARE")
                Log.d("AudioRecorder", "Calling prepare()...")
                prepare()
                Log.d("RECORD_DEBUG", "MEDIA_RECORDER_PREPARE_SUCCESS")
                
                Log.d("RECORD_DEBUG", "MEDIA_RECORDER_START")
                Log.d("AudioRecorder", "Calling start()...")
                start()
                Log.d("RECORD_DEBUG", "MEDIA_RECORDER_START_SUCCESS")
            }
            _isRecording = true
            Log.d("AudioRecorder", "Recording started: ${outputFile.absolutePath}")
        } catch (e: Exception) {
            Log.e("RECORD_DEBUG", "MEDIA_RECORDER_ERROR exception=${e.javaClass.simpleName} message=${e.message}")
            Log.e("AudioRecorder", "Failed to start recording", e)
            _isRecording = false
            recorder?.release()
            recorder = null
        }
    }

    fun stop() {
        try {
            recorder?.apply {
                stop()
                release()
            }
            Log.d("AudioRecorder", "Recording stopped")
        } catch (e: Exception) {
            Log.e("AudioRecorder", "Error stopping recorder", e)
        } finally {
            recorder = null
            _isRecording = false
        }
    }

    fun release() {
        stop()
    }
}
