package com.rahul.fieldflow.data.reports

import com.rahul.fieldflow.data.recording.RecordingSessionDto
import com.rahul.fieldflow.data.tasks.TaskDto
import com.rahul.fieldflow.domain.model.ActionItem
import com.rahul.fieldflow.domain.model.KeyFinding
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
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

    suspend fun getEmployeeReports(userId: String): List<TaskReportWithDetailsDto> {
        android.util.Log.d("REPORT_DEBUG", "MY_REPORTS_DATASOURCE_START userId=$userId")
        return try {
            val response = supabaseClient.postgrest["task_reports"]
                .select(Columns.raw("*, tasks!inner(*, task_assignments!inner(*, profiles:profiles!task_assignments_employee_id_fkey(*)), recording_sessions(*))")) {
                    filter {
                        eq("tasks.task_assignments.employee_id", userId)
                    }
                }
            
            android.util.Log.d("REPORT_DEBUG", "MY_REPORTS_DATASOURCE_RAW: ${response.data}")
            val result = response.decodeList<TaskReportWithDetailsDto>()
            android.util.Log.d("REPORT_DEBUG", "MY_REPORTS_DATASOURCE_COUNT: ${result.size}")
            result
        } catch (e: Exception) {
            android.util.Log.e("REPORT_DEBUG", "MY_REPORTS_DATASOURCE_ERROR", e)
            emptyList()
        }
    }

    suspend fun getOwnerReports(workspaceId: String): List<TaskReportWithDetailsDto> {
        android.util.Log.d("REPORT_DEBUG", "OWNER_REPORTS_DATASOURCE_START workspaceId=$workspaceId")
        return try {
            val response = supabaseClient.postgrest["task_reports"]
                .select(Columns.raw("*, tasks!inner(*, task_assignments!inner(*, profiles:profiles!task_assignments_employee_id_fkey!inner(*)), recording_sessions(*))")) {
                    filter {
                        eq("tasks.task_assignments.profiles.workspace_id", workspaceId)
                    }
                }
            
            android.util.Log.d("REPORT_DEBUG", "OWNER_REPORTS_DATASOURCE_RAW: ${response.data}")
            val result = response.decodeList<TaskReportWithDetailsDto>()
            android.util.Log.d("REPORT_DEBUG", "OWNER_REPORTS_DATASOURCE_COUNT: ${result.size}")
            result
        } catch (e: Exception) {
            android.util.Log.e("REPORT_DEBUG", "OWNER_REPORTS_DATASOURCE_ERROR", e)
            emptyList()
        }
    }

    suspend fun updateReportStatus(reportId: String, status: String) {
        android.util.Log.d("REPORT_DEBUG", "UPDATE_REPORT_STATUS_START reportId=$reportId status=$status")
        if (reportId.isEmpty()) {
            android.util.Log.e("REPORT_DEBUG", "UPDATE_REPORT_STATUS_FAILED: reportId is empty")
            return
        }
        try {
            supabaseClient.postgrest["task_reports"].update(
                buildJsonObject {
                    put("status", status)
                    put("updated_at", java.time.OffsetDateTime.now().toString())
                }
            ) {
                filter {
                    eq("id", reportId)
                }
            }
            
            // Verification step as per plan
            val verifyResponse = supabaseClient.postgrest["task_reports"]
                .select {
                    filter {
                        eq("id", reportId)
                    }
                }
            val updatedReport = verifyResponse.decodeList<TaskReportDto>().firstOrNull()
            if (updatedReport?.status == status) {
                android.util.Log.d("REPORT_DEBUG", "UPDATE_REPORT_STATUS_SUCCESS")
            } else {
                android.util.Log.e("REPORT_DEBUG", "UPDATE_REPORT_STATUS_VERIFY_FAILED: status is ${updatedReport?.status}")
                throw Exception("Status update verification failed")
            }
        } catch (e: Exception) {
            android.util.Log.e("REPORT_DEBUG", "UPDATE_REPORT_STATUS_ERROR", e)
            throw e
        }
    }
}

@Serializable
data class TaskReportWithDetailsDto(
    @SerialName("id") val id: String,
    @SerialName("task_id") val taskId: String,
    val summary: String? = null,
    @SerialName("key_findings") val keyFindings: List<KeyFinding>? = null,
    @SerialName("action_items") val actionItems: List<ActionItem>? = null,
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
    @SerialName("key_findings") val keyFindings: List<KeyFinding>? = null,
    @SerialName("action_items") val actionItems: List<ActionItem>? = null,
    val status: String,
    val version: Int,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String
)
