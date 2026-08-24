package com.rahul.fieldflow.data.tasks

import com.rahul.fieldflow.data.auth.ProfileDto
import com.rahul.fieldflow.domain.model.AssignmentStatus
import com.rahul.fieldflow.domain.model.TaskAssignment
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class TaskAssignmentDto(
    @SerialName("id") val id: String? = null,
    @SerialName("task_id") val taskId: String,
    @SerialName("employee_id") val employeeId: String,
    @SerialName("assigned_by") val assignedBy: String,
    @SerialName("assigned_at") val assignedAt: String? = null,
    @SerialName("status") val status: String,
    @SerialName("profiles") val employeeProfile: ProfileDto? = null
) {
    fun toDomain(): TaskAssignment {
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        return TaskAssignment(
            id = id.orEmpty(),
            taskId = taskId,
            employeeId = employeeId,
            assignedBy = assignedBy,
            assignedAt = assignedAt?.let { OffsetDateTime.parse(it, formatter) } ?: OffsetDateTime.now(),
            status = AssignmentStatus.valueOf(status.uppercase())
        )
    }
}
