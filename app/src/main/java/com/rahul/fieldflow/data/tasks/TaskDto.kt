package com.rahul.fieldflow.data.tasks

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
    val title: String,

    @SerialName("description")
    val description: String? = null,

    @SerialName("status")
    val status: String,

    @SerialName("priority")
    val priority: String,

    @SerialName("created_by")
    val createdBy: String,

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
    val assignments: List<TaskAssignmentDto> = emptyList()
) {

    fun toDomain(): Task {
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

        return Task(
            id = id.orEmpty(),
            title = title,
            description = description,

            status = TaskStatus.valueOf(
                status.uppercase()
            ),

            priority = TaskPriority.valueOf(
                priority.uppercase()
            ),

            createdBy = createdBy,

            location = location,
            latitude = latitude,
            longitude = longitude,
            radiusMeters = radiusMeters ?: 50,

            dueDate = dueDate?.let {
                OffsetDateTime.parse(it, formatter)
            },

            completedAt = completedAt?.let {
                OffsetDateTime.parse(it, formatter)
            },

            isDeleted = isDeleted,

            createdAt = createdAt?.let {
                OffsetDateTime.parse(it, formatter)
            } ?: OffsetDateTime.now(),

            updatedAt = updatedAt?.let {
                OffsetDateTime.parse(it, formatter)
            } ?: OffsetDateTime.now(),

            assignedEmployee =
                assignments
                    .firstOrNull()
                    ?.employeeProfile
                    ?.toDomain()
        )
    }
}