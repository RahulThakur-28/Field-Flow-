package com.rahul.fieldflow.data.tasks

import android.util.Log
import com.rahul.fieldflow.domain.model.Task
import com.rahul.fieldflow.domain.model.TaskPriority
import com.rahul.fieldflow.domain.model.TaskStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class TaskDto(
    @SerialName("id")
    val id: String? = null,

    @SerialName("title")
    val title: String? = null,

    @SerialName("description")
    val description: String? = null,

    @SerialName("status")
    val status: String? = null,

    @SerialName("priority")
    val priority: String? = null,

    @SerialName("created_by")
    val createdBy: String? = null,

    @SerialName("location")
    val location: String? = null,

    @SerialName("latitude")
    val latitude: Double? = null,

    @SerialName("longitude")
    val longitude: Double? = null,

    @SerialName("radius_meters")
    val radiusMeters: Int? = null,

    @SerialName("due_date")
    val dueDate: String? = null,

    @SerialName("completed_at")
    val completedAt: String? = null,

    @SerialName("is_deleted")
    val isDeleted: Boolean = false,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null,

    @SerialName("task_assignments")
    val assignments: List<TaskAssignmentDto> = emptyList(),

    @SerialName("task_checklist_items")
    val checklistItems: List<TaskChecklistItemDto> = emptyList(),

    @SerialName("geofences")
    val geofence: GeofenceDto? = null
) {

    fun toDomain(): Task {
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

        return try {
            Task(
                id = id.orEmpty(),
                title = title ?: "Untitled Task",
                description = description,

                status = try {
                    TaskStatus.valueOf(status?.uppercase() ?: "PENDING")
                } catch (e: Exception) {
                    TaskStatus.PENDING
                },

                priority = try {
                    TaskPriority.valueOf(priority?.uppercase() ?: "MEDIUM")
                } catch (e: Exception) {
                    TaskPriority.MEDIUM
                },

                createdBy = createdBy.orEmpty(),

                location = location,
                latitude = latitude ?: geofence?.latitude,
                longitude = longitude ?: geofence?.longitude,
                radiusMeters = radiusMeters ?: geofence?.radiusMeters ?: 50,

                dueDate = dueDate?.let {
                    runCatching { OffsetDateTime.parse(it, formatter) }.getOrNull()
                },

                completedAt = completedAt?.let {
                    runCatching { OffsetDateTime.parse(it, formatter) }.getOrNull()
                },

                isDeleted = isDeleted,

                createdAt = createdAt?.let {
                    runCatching { OffsetDateTime.parse(it, formatter) }.getOrNull()
                } ?: OffsetDateTime.now(),

                updatedAt = updatedAt?.let {
                    runCatching { OffsetDateTime.parse(it, formatter) }.getOrNull()
                } ?: OffsetDateTime.now(),

                assignedEmployee =
                assignments
                    .firstOrNull()
                    ?.employeeProfile
                    ?.toDomain(),

                checklist = checklistItems
                    .sortedBy { it.position }
                    .map { it.toDomain() }
            ).also { 
                Log.d("OWNER_CHECKLIST_TRACE", "TaskDto.toDomain: domain checklist count=${it.checklist.size}")
                it.checklist.forEach { item ->
                    Log.d("OWNER_CHECKLIST_TRACE", "Domain Checklist Item: id=${item.id}, itemText=${item.itemText}, isCompleted=${item.isCompleted}, position=${item.position}")
                }
            }
        } catch (e: Exception) {
            Log.e("OWNER_CHECKLIST_TRACE", "Error mapping TaskDto to Domain: class=${e.javaClass.simpleName}, message=${e.message}", e)
            // Return a fallback task instead of throwing, to keep the list working
            Task(
                id = id.orEmpty(),
                title = "Error loading task",
                description = e.message,
                status = TaskStatus.PENDING,
                priority = TaskPriority.LOW,
                createdBy = "",
                location = null,
                latitude = null,
                longitude = null,
                radiusMeters = 50,
                dueDate = null,
                completedAt = null,
                isDeleted = false,
                createdAt = OffsetDateTime.now(),
                updatedAt = OffsetDateTime.now()
            )
        }
    }
}

@Serializable
data class GeofenceDto(
    @SerialName("id") val id: String,
    @SerialName("task_id") val taskId: String,
    @SerialName("latitude") val latitude: Double,
    @SerialName("longitude") val longitude: Double,
    @SerialName("radius_meters") val radiusMeters: Int,
    @SerialName("is_active") val isActive: Boolean = true
)
