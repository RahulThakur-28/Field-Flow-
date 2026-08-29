package com.rahul.fieldflow.domain.repository

import com.rahul.fieldflow.domain.model.RecordingSession
import com.rahul.fieldflow.domain.model.Transcript
import java.io.File
import java.time.OffsetDateTime

interface RecordingRepository {
    suspend fun createSession(taskId: String): Result<RecordingSession>
    suspend fun updateSessionStatus(sessionId: String, status: String, endedAt: OffsetDateTime? = null, durationSeconds: Int? = null): Result<Unit>
    suspend fun uploadRecording(taskId: String, sessionId: String, audioFile: File): Result<String>
    suspend fun triggerTranscription(sessionId: String): Result<Unit>
    suspend fun getSessionsByTaskId(taskId: String): Result<List<RecordingSession>>
    suspend fun getTranscriptsByTaskId(taskId: String): Result<List<Transcript>>
    suspend fun getSignedUrl(storagePath: String): Result<String>
}
