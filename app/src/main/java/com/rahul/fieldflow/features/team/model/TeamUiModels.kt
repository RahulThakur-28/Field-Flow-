package com.rahul.fieldflow.features.team.model

import com.rahul.fieldflow.domain.model.Task
import com.rahul.fieldflow.domain.model.UserProfile
import com.rahul.fieldflow.domain.model.TaskStatus as DomainTaskStatus
import com.rahul.fieldflow.features.tasks.model.TaskStatus as UiTaskStatus

data class EmployeeTeamUiModel(
    val profile: UserProfile,
    val totalTasks: Int,
    val completedTasks: Int,
    val currentTask: Task?,
    val status: String // "Active" or "Idle" derived from currentTask
)

fun DomainTaskStatus.toUiStatus(): UiTaskStatus {
    return when (this) {
        DomainTaskStatus.PENDING -> UiTaskStatus.PENDING
        DomainTaskStatus.ASSIGNED -> UiTaskStatus.ASSIGNED
        DomainTaskStatus.IN_PROGRESS -> UiTaskStatus.IN_PROGRESS
        DomainTaskStatus.COMPLETED -> UiTaskStatus.COMPLETED
        DomainTaskStatus.CANCELLED -> UiTaskStatus.CANCELLED
    }
}
