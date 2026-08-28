package com.rahul.fieldflow.domain.model

import java.time.OffsetDateTime

data class Task(
    val id: String,
    val title: String,
    val description: String?,
    val status: TaskStatus,
    val priority: TaskPriority,
    val createdBy: String,
    val location: String?,
    val latitude: Double?,
    val longitude: Double?,
    val radiusMeters: Int,
    val dueDate: OffsetDateTime?,
    val completedAt: OffsetDateTime?,
    val isDeleted: Boolean,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
    val assignedEmployee: UserProfile? = null,
    val checklist: List<TaskChecklistItem> = emptyList()
)

enum class TaskStatus {
    PENDING, ASSIGNED, IN_PROGRESS, COMPLETED, CANCELLED
}

enum class TaskPriority {
    LOW, MEDIUM, HIGH, URGENT
}
