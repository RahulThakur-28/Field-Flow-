package com.rahul.fieldflow.data.reports

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.postgrest
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
        return supabaseClient.postgrest["task_reports"]
            .select {
                filter {
                    eq("task_id", taskId)
                }
            }
            .decodeSingleOrNull<TaskReportDto>()
    }

    suspend fun triggerReportGeneration(taskId: String) {
        supabaseClient.functions.invoke(
            function = "generate-report",
            body = buildJsonObject {
                put("task_id", taskId)
            }
        )
    }
}

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
