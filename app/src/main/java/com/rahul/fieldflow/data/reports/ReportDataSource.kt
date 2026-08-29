package com.rahul.fieldflow.data.reports

import com.rahul.fieldflow.data.recording.RecordingSessionDto
import com.rahul.fieldflow.data.tasks.TaskDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject

class ReportDataSource @Inject constructor(
    private val supabaseClient: SupabaseClient
) {
    suspend fun getTaskReport(taskId: String): TaskReportDto? {
        android.util.Log.d("REPORT_DEBUG", "REPORT_DATASOURCE_START taskId=$taskId")
        return try {
            val response = supabaseClient.postgrest["task_reports"]
                .select {
                    filter {
                        eq("task_id", taskId)
                    }
                }
            android.util.Log.d("REPORT_DEBUG", "REPORT_DATASOURCE_RAW_RESPONSE: ${response.data}")
            // Using decodeList().firstOrNull() as it's often more robust than decodeSingleOrNull
            val result = response.decodeList<TaskReportDto>().firstOrNull()
            android.util.Log.d("REPORT_DEBUG", "REPORT_DATASOURCE_RESULT found=${result != null}")
            result
        } catch (e: Exception) {
            android.util.Log.e("REPORT_DEBUG", "REPORT_DATASOURCE_ERROR", e)
            null
        }
    }

    suspend fun triggerReportGeneration(taskId: String) {
        supabaseClient.functions.invoke(
            function = "generate-report",
            body = buildJsonObject {
                put("task_id", taskId)
            }
        )
    }

    suspend fun getEmployeeReports(): List<TaskReportWithDetailsDto> {
        // Query tasks assigned to employee that have a report
        // Join tasks -> task_assignments -> profiles (to get employee info)
        // Join tasks -> recording_sessions (to get duration)
        // Explicitly specify the relationship to profiles to avoid ambiguity (PGRST201)
        return supabaseClient.postgrest["task_reports"]
            .select(Columns.raw("*, tasks(*, task_assignments(*, profiles:profiles!task_assignments_employee_id_fkey(*)), recording_sessions(*))"))
            .decodeList<TaskReportWithDetailsDto>()
    }
}

@Serializable
data class TaskReportWithDetailsDto(
    @SerialName("id") val id: String,
    @SerialName("task_id") val taskId: String,
    val summary: String? = null,
    @SerialName("key_findings") val keyFindings: JsonArray? = null,
    @SerialName("action_items") val actionItems: JsonArray? = null,
    val status: String,
    val version: Int,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
    val tasks: TaskDto? = null
)

@Serializable
data class TaskReportDto(
    val id: String,
    @SerialName("task_id") val taskId: String,
    val summary: String? = null,
    @SerialName("key_findings") val keyFindings: JsonArray? = null,
    @SerialName("action_items") val actionItems: JsonArray? = null,
    val status: String,
    val version: Int,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String
)
