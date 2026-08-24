package com.rahul.fieldflow.data.tasks

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject

class TaskDataSource @Inject constructor(
    private val supabaseClient: SupabaseClient
) {
    suspend fun createTask(
        title: String,
        description: String?,
        priority: String,
        createdBy: String,
        location: String?,
        dueDate: String?,
        employeeId: String,
        latitude: Double?,
        longitude: Double?,
        radiusMeters: Int?
    ) {
        try {
            Log.d(
                "TASK_CREATE_DEBUG",
                """
            Calling create_task_v1
            title=$title
            description=$description
            priority=$priority
            createdBy=$createdBy
            location=$location
            dueDate=$dueDate
            employeeId=$employeeId
            latitude=$latitude
            longitude=$longitude
            radiusMeters=$radiusMeters
            """.trimIndent()
            )

            supabaseClient.postgrest.rpc(
                function = "create_task_v1",
                parameters = buildJsonObject {
                    put("p_title", title)
                    put("p_description", description)
                    put("p_priority", priority)
                    put("p_created_by", createdBy)
                    put("p_location", location)
                    put("p_due_date", dueDate)
                    put("p_employee_id", employeeId)
                    put("p_latitude", latitude)
                    put("p_longitude", longitude)
                    put("p_radius_meters", radiusMeters)
                }
            )

            Log.d(
                "TASK_CREATE_DEBUG",
                "create_task_v1 RPC SUCCESS"
            )

        } catch (e: Exception) {
            Log.e(
                "TASK_CREATE_DEBUG",
                "create_task_v1 RPC FAILED: ${e.message}",
                e
            )
            throw e
        }
    }

    suspend fun getTasksCreatedBy(ownerId: String): List<TaskDto> {
        return supabaseClient.postgrest["tasks"]
            .select(
                Columns.raw(
                    "*, task_assignments(*, profiles!task_assignments_employee_id_fkey(*))"
                )
            ) {
                filter {
                    eq("created_by", ownerId)
                    eq("is_deleted", false)
                }
            }
            .decodeList<TaskDto>()
    }
    suspend fun getTaskById(taskId: String): TaskDto {
        return supabaseClient.postgrest["tasks"]
            .select(
                Columns.raw(
                    "*, task_assignments(*, profiles!task_assignments_employee_id_fkey(*))"
                )
            ) {
                filter {
                    eq("id", taskId)
                    eq("is_deleted", false)
                }
            }
            .decodeSingle<TaskDto>()
    }


}
