package com.rahul.fieldflow.data.recording

import android.util.Log
import com.rahul.fieldflow.data.auth.AuthDataSource
import com.rahul.fieldflow.domain.model.RecordingSession
import com.rahul.fieldflow.domain.model.Transcript
import com.rahul.fieldflow.domain.model.TranscriptSegment
import com.rahul.fieldflow.domain.repository.RecordingRepository
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecordingRepositoryImpl @Inject constructor(
    private val recordingDataSource: RecordingDataSource,
    private val authDataSource: AuthDataSource
) : RecordingRepository {

    override suspend fun createSession(taskId: String): Result<RecordingSession> {
        return runCatching {
            Log.d("RecordingRepo", "createSession taskId=$taskId")
            val employeeId = authDataSource.getCurrentUserId() ?: throw Exception("Not logged in")
            Log.d("RecordingRepo", "employeeId=$employeeId, calling dataSource.createSession")
            val dto = recordingDataSource.createSession(taskId, employeeId)
            Log.d("RecordingRepo", "dataSource.createSession returned dto id=${dto.id}")
            
            // PRODUCTION HARDENING: Log activity for traceability
            try {
                recordingDataSource.logActivity(taskId, employeeId, "recording_started", mapOf("session_id" to dto.id))
            } catch (e: Exception) {
                Log.e("RecordingRepo", "Activity logging failed", e)
            }
            
            dto.toDomain()
        }.onFailure { e ->
            Log.e("RecordingRepo", "createSession FAILED", e)
        }
    }

    override suspend fun updateSessionStatus(sessionId: String, status: String, endedAt: OffsetDateTime?, durationSeconds: Int?): Result<Unit> {
        return runCatching {
            recordingDataSource.updateSessionStatus(sessionId, status, endedAt, durationSeconds)
        }
    }

    override suspend fun uploadRecording(taskId: String, sessionId: String, audioFile: File): Result<String> {
        return runCatching {
            val employeeId = authDataSource.getCurrentUserId() ?: throw Exception("Not logged in")
            val fileName = audioFile.name
            val bytes = audioFile.readBytes()
            val storagePath = recordingDataSource.uploadRecording(taskId, employeeId, fileName, bytes)
            recordingDataSource.updateSessionStoragePath(sessionId, storagePath)
            
            // PRODUCTION HARDENING: Cleanup local file ONLY after successful upload
            if (audioFile.exists()) {
                audioFile.delete()
            }
            
            storagePath
        }
    }

    override suspend fun triggerTranscription(sessionId: String): Result<Unit> {
        return runCatching {
            recordingDataSource.triggerTranscription(sessionId)
        }
    }

    override suspend fun getSessionsByTaskId(taskId: String): Result<List<RecordingSession>> {
        return runCatching {
            recordingDataSource.getSessions(taskId).map { it.toDomain() }
        }
    }

    override suspend fun getTranscriptsByTaskId(taskId: String): Result<List<Transcript>> {
        return runCatching {
            recordingDataSource.getTranscriptsForTask(taskId).map { it.toDomain() }
        }
    }

    override suspend fun getSignedUrl(storagePath: String): Result<String> {
        return runCatching {
            recordingDataSource.getSignedUrl(storagePath)
        }
    }

    private fun RecordingSessionDto.toDomain() = RecordingSession(
        id = id,
        taskId = taskId,
        employeeId = employeeId,
        startedAt = OffsetDateTime.parse(startedAt),
        endedAt = endedAt?.let { OffsetDateTime.parse(it) },
        status = status,
        storagePath = storagePath,
        durationSeconds = durationSeconds
    )

    private fun TranscriptDto.toDomain() = Transcript(
        id = id,
        recordingSessionId = recordingSessionId,
        text = text,
        segments = segments?.map { 
            val obj = it.jsonObject
            TranscriptSegment(
                start = obj["start"]?.jsonPrimitive?.double ?: 0.0,
                end = obj["end"]?.jsonPrimitive?.double ?: 0.0,
                text = obj["text"]?.jsonPrimitive?.content ?: "",
                speaker = obj["speaker"]?.jsonPrimitive?.content
            )
        } ?: emptyList(),
        language = language,
        status = status,
        createdAt = OffsetDateTime.parse(createdAt)
    )
}
