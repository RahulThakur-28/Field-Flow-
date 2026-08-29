package com.rahul.fieldflow.data.recording

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes

class RecordingDataSource @Inject constructor(
    private val supabaseClient: SupabaseClient
) {
    suspend fun triggerTranscription(sessionId: String) {
        try {
            supabaseClient.functions.invoke(
                function = "process-audio",
                body = buildJsonObject {
                    put("recording_session_id", sessionId)
                }
            )
        } catch (e: Exception) {
            Log.e("RecordingDataSource", "Failed to trigger transcription for session $sessionId", e)
        }
    }

    suspend fun createSession(taskId: String, employeeId: String): RecordingSessionDto {
        Log.d("RecordingDataSource", "createSession: task=$taskId, employee=$employeeId")
        try {
            val response = supabaseClient.postgrest["recording_sessions"].insert(
                buildJsonObject {
                    put("task_id", taskId)
                    put("employee_id", employeeId)
                    put("status", "recording")
                }
            ) {
                select()
            }
            Log.d("RecordingDataSource", "INSERT SUCCESS: ${response.data}")
            return response.decodeSingle<RecordingSessionDto>()
        } catch (e: Exception) {
            Log.e("RecordingDataSource", "INSERT FAILED", e)
            throw e
        }
    }

    suspend fun updateSessionStatus(sessionId: String, status: String, endedAt: OffsetDateTime? = null, durationSeconds: Int? = null) {
        supabaseClient.postgrest["recording_sessions"].update(
            buildJsonObject {
                put("status", status)
                endedAt?.let {
                    put("ended_at", it.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                }
                durationSeconds?.let {
                    put("duration_seconds", it)
                }
            }
        ) {
            filter {
                eq("id", sessionId)
            }
        }
    }

    suspend fun uploadRecording(taskId: String, employeeId: String, fileName: String, bytes: ByteArray): String {
        val path = "$taskId/$employeeId/$fileName"
        supabaseClient.storage["recordings"].upload(
            path = path,
            data = bytes
        ) {
            upsert = true
        }
        return path
    }

    suspend fun updateSessionStoragePath(sessionId: String, storagePath: String) {
        supabaseClient.postgrest["recording_sessions"].update(
            buildJsonObject {
                put("storage_path", storagePath)
            }
        ) {
            filter {
                eq("id", sessionId)
            }
        }
    }

    suspend fun getSessions(taskId: String): List<RecordingSessionDto> {
        return supabaseClient.postgrest["recording_sessions"]
            .select {
                filter {
                    eq("task_id", taskId)
                }
                order("started_at", order = Order.ASCENDING)
            }
            .decodeList<RecordingSessionDto>()
    }

    suspend fun getTranscriptsForTask(taskId: String): List<TranscriptDto> {
        return supabaseClient.postgrest["transcripts"]
            .select(Columns.raw("*, recording_sessions!inner(task_id)")) {
                filter {
                    eq("recording_sessions.task_id", taskId)
                }
            }
            .decodeList<TranscriptDto>()
    }

    suspend fun getSignedUrl(storagePath: String): String {
        return supabaseClient.storage["recordings"].createSignedUrl(
            path = storagePath,
            expiresIn = 60.minutes
        )
    }

    suspend fun logActivity(taskId: String, userId: String, action: String, metadata: Map<String, String> = emptyList<Pair<String, String>>().toMap()) {
        try {
            supabaseClient.postgrest["activity_logs"].insert(
                buildJsonObject {
                    put("task_id", taskId)
                    put("user_id", userId)
                    put("action", action)
                    put("metadata", buildJsonObject {
                        metadata.forEach { (k, v) -> put(k, v) }
                    })
                }
            )
        } catch (e: Exception) {
            Log.e("RecordingDataSource", "Failed to log activity: $action", e)
        }
    }
}

@Serializable
data class RecordingSessionDto(
    val id: String,
    @SerialName("task_id") val taskId: String,
    @SerialName("employee_id") val employeeId: String,
    @SerialName("started_at") val startedAt: String,
    @SerialName("ended_at") val endedAt: String? = null,
    val status: String,
    @SerialName("storage_path") val storagePath: String? = null,
    @SerialName("duration_seconds") val durationSeconds: Int? = null
)

@Serializable
data class TranscriptDto(
    val id: String,
    @SerialName("recording_session_id") val recordingSessionId: String,
    val text: String,
    val segments: JsonArray? = null,
    val language: String? = null,
    val status: String,
    @SerialName("created_at") val createdAt: String
)
