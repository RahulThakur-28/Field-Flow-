package com.rahul.fieldflow.data.tasks

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
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
        radiusMeters: Int?,
        checklistItems: List<String> = emptyList()
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
            checklistCount=${checklistItems.size}
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
                    put("p_checklist_items", JsonArray(checklistItems.map { JsonPrimitive(it) }))
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

    suspend fun updateTask(
        taskId: String,
        title: String,
        description: String?,
        priority: String,
        location: String?,
        dueDate: String?,
        employeeId: String,
        latitude: Double?,
        longitude: Double?,
        radiusMeters: Int?,
        checklistItems: List<String> = emptyList()
    ) {
        try {
            supabaseClient.postgrest.rpc(
                function = "update_task_v1",
                parameters = buildJsonObject {
                    put("p_task_id", taskId)
                    put("p_title", title)
                    put("p_description", description)
                    put("p_priority", priority)
                    put("p_due_date", dueDate)
                    put("p_employee_id", employeeId)
                    put("p_location", location)
                    put("p_latitude", latitude)
                    put("p_longitude", longitude)
                    put("p_radius_meters", radiusMeters)
                    put("p_checklist_items", JsonArray(checklistItems.map { JsonPrimitive(it) }))
                }
            )
        } catch (e: Exception) {
            Log.e("TASK_UPDATE_DEBUG", "update_task_v1 RPC FAILED", e)
            throw e
        }
    }

    suspend fun getTasksCreatedBy(ownerId: String): List<TaskDto> {
        return try {
            Log.d("TEAM_DATA_DEBUG", "getTasksCreatedBy: owner query started for ownerId = $ownerId")
            
            val response = supabaseClient.postgrest["tasks"]
                .select(
                    Columns.raw(
                        "*, task_assignments(*, profiles:profiles!employee_id(*)), task_checklist_items(*), geofences(*), task_reports(*), recording_sessions(*)"
                    )
                ) {
                    filter {
                        eq("created_by", ownerId)
                        eq("is_deleted", false)
                    }
                }
            
            Log.d("TEAM_DATA_DEBUG", "getTasksCreatedBy: raw response = ${response.data}")
            
            val tasks = response.decodeList<TaskDto>()
            Log.d("TEAM_DATA_DEBUG", "getTasksCreatedBy: decoded count = ${tasks.size}")
            tasks
        } catch (e: Exception) {
            Log.e("TEAM_DATA_DEBUG", "getTasksCreatedBy: error = ${e.message}", e)
            throw e
        }
    }

    suspend fun getTasksForEmployee(employeeId: String): List<TaskDto> {
        return try {
            Log.d("TEAM_DATA_DEBUG", "getTasksForEmployee: employeeId=$employeeId")
            val response = supabaseClient.postgrest["tasks"]
                .select(
                    Columns.raw(
                        "*, task_assignments!inner(*, profiles:profiles!employee_id(*)), task_checklist_items(*), geofences(*), task_reports(*), recording_sessions(*)"
                    )
                ) {
                    filter {
                        eq("task_assignments.employee_id", employeeId)
                        eq("is_deleted", false)
                    }
                }
            
            Log.d("TEAM_DATA_DEBUG", "getTasksForEmployee: raw=${response.data}")
            val list = response.decodeList<TaskDto>()
            Log.d("TEAM_DATA_DEBUG", "getTasksForEmployee: count=${list.size}")
            list
        } catch (e: Exception) {
            Log.e("TEAM_DATA_DEBUG", "getTasksForEmployee error: ${e.message}", e)
            throw e
        }
    }

    suspend fun getTaskById(taskId: String): TaskDto {
        return try {
            val session = supabaseClient.auth.currentSessionOrNull()
            val currentUserId = supabaseClient.auth.currentUserOrNull()?.id
            
            Log.d("DIRECT_CHECKLIST_DEBUG", "clientInitialized = true")
            Log.d("DIRECT_CHECKLIST_DEBUG", "sessionExists = ${session != null}")
            Log.d("DIRECT_CHECKLIST_DEBUG", "authenticatedUserId = $currentUserId")
            Log.d("DIRECT_CHECKLIST_DEBUG", "taskIdUsed = $taskId")
            Log.d("DIRECT_CHECKLIST_DEBUG", "taskIdStringLength = ${taskId.length}")

            // 1. EMBEDDED QUERY
            Log.d("OWNER_CHECKLIST_TRACE", "getTaskById: taskId=$taskId checklist query started")
            val response = supabaseClient.postgrest["tasks"]
                .select(
                    Columns.raw(
                        "*, task_assignments(*, profiles:profiles!employee_id(*)), task_checklist_items(*), geofences(*), task_reports(*), recording_sessions(*)"
                    )
                ) {
                    filter {
                        eq("id", taskId)
                        eq("is_deleted", false)
                    }
                }
            
            Log.d("OWNER_CHECKLIST_TRACE", "getTaskById: embedded task query succeeded")
            val taskDto = response.decodeSingle<TaskDto>()
            Log.d("OWNER_CHECKLIST_TRACE", "getTaskById: task.created_by=${taskDto.createdBy}")
            Log.d("OWNER_CHECKLIST_TRACE", "getTaskById: embedded checklist count=${taskDto.checklistItems.size}")
            
            // 2. DIRECT DIAGNOSTIC QUERY
            Log.d("OWNER_CHECKLIST_TRACE", "getTaskById: direct checklist query starting...")
            try {
                Log.d("DIRECT_CHECKLIST_DEBUG", "table = task_checklist_items")
                Log.d("DIRECT_CHECKLIST_DEBUG", "filterColumn = task_id")
                Log.d("DIRECT_CHECKLIST_DEBUG", "filterValue = $taskId")

                val directResponse = supabaseClient.postgrest["task_checklist_items"]
                    .select {
                        filter {
                            eq("task_id", taskId)
                        }
                    }
                
                Log.d("DIRECT_CHECKLIST_DEBUG", "querySuccess = true")
                Log.d("DIRECT_CHECKLIST_DEBUG", "rawRows = ${directResponse.data}")
                
                val directChecklistRows = directResponse.decodeList<TaskChecklistItemDto>()
                Log.d("DIRECT_CHECKLIST_DEBUG", "decodedRows = ${directChecklistRows.size}")
                Log.d("OWNER_CHECKLIST_TRACE", "getTaskById: direct checklist rows returned=${directChecklistRows.size}")
                
                directChecklistRows.forEach { item ->
                    Log.d("OWNER_CHECKLIST_TRACE", "Direct Checklist Row: id=${item.id}, task_id=${item.taskId}, item_text=${item.itemText}, is_completed=${item.isCompleted}, position=${item.position}")
                }
            } catch (de: Exception) {
                Log.e("DIRECT_CHECKLIST_DEBUG", "querySuccess = false")
                Log.e("DIRECT_CHECKLIST_DEBUG", "exceptionClass = ${de.javaClass.simpleName}")
                Log.e("DIRECT_CHECKLIST_DEBUG", "exceptionMessage = ${de.message}")
                if (de is RestException) {
                    Log.e("DIRECT_CHECKLIST_DEBUG", "PostgREST Error Detail: $de")
                }
            }
            
            taskDto
        } catch (e: Exception) {
            Log.e("OWNER_CHECKLIST_TRACE", "getTaskById: exception=${e.javaClass.simpleName}, message=${e.message}")
            if (e is RestException) {
                Log.e("OWNER_CHECKLIST_TRACE", "getTaskById: RestException=$e")
            }
            throw e
        }
    }

    suspend fun updateChecklistItemCompletion(itemId: String, isCompleted: Boolean) {
        supabaseClient.postgrest["task_checklist_items"].update(
            buildJsonObject {
                put("is_completed", isCompleted)
            }
        ) {
            filter {
                eq("id", itemId)
            }
        }
    }

    suspend fun startTask(taskId: String): TaskDto {
        return try {
            val response = supabaseClient.postgrest.rpc(
                function = "start_task",
                parameters = buildJsonObject {
                    put("p_task_id", taskId)
                }
            )
            Log.d("TASK_RPC_DEBUG", "start_task raw response: ${response.data}")
            response.decodeAs<TaskDto>()
        } catch (e: Exception) {
            Log.e("TASK_RPC_DEBUG", "start_task RPC FAILED: ${e.message}", e)
            throw e
        }
    }

    suspend fun completeTask(taskId: String): TaskDto {
        return try {
            val response = supabaseClient.postgrest.rpc(
                function = "complete_task",
                parameters = buildJsonObject {
                    put("p_task_id", taskId)
                }
            )
            Log.d("TASK_RPC_DEBUG", "complete_task raw response: ${response.data}")
            response.decodeAs<TaskDto>()
        } catch (e: Exception) {
            Log.e("TASK_RPC_DEBUG", "complete_task RPC FAILED: ${e.message}", e)
            throw e
        }
    }


}
