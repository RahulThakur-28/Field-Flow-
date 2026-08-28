package com.rahul.fieldflow.data.tasks

import android.util.Log
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
    @SerialName("task_id") val taskId: String? = null,
    @SerialName("employee_id") val employeeId: String? = null,
    @SerialName("assigned_by") val assignedBy: String? = null,
    @SerialName("assigned_at") val assignedAt: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("profiles") val employeeProfile: ProfileDto? = null
) {
    fun toDomain(): TaskAssignment {
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        return TaskAssignment(
            id = id.orEmpty(),
            taskId = taskId.orEmpty(),
            employeeId = employeeId.orEmpty(),
            assignedBy = assignedBy.orEmpty(),
            assignedAt = assignedAt?.let { 
                runCatching { OffsetDateTime.parse(it, formatter) }.getOrNull() 
            } ?: OffsetDateTime.now(),
            status = try {
                AssignmentStatus.valueOf(status?.uppercase() ?: "ASSIGNED")
            } catch (e: Exception) {
                AssignmentStatus.ASSIGNED
            }
        )
    }
}
