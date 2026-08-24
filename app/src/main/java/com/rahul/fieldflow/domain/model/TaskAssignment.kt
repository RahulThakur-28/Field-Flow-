package com.rahul.fieldflow.domain.model

import java.time.OffsetDateTime

data class TaskAssignment(
    val id: String,
    val taskId: String,
    val employeeId: String,
    val assignedBy: String,
    val assignedAt: OffsetDateTime,
    val status: AssignmentStatus
)

enum class AssignmentStatus {
    ASSIGNED, ACCEPTED, IN_PROGRESS, COMPLETED, CANCELLED
}
