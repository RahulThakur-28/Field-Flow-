package com.rahul.fieldflow.features.home.owner.state

import com.rahul.fieldflow.domain.model.*

data class OwnerHomeUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val userName: String = "",
    val initials: String = "",
    val companyName: String = "",
    val companyId: String = "",
    val totalTasksCount: Int = 0,
    val activeTasksCount: Int = 0,
    val completedTasksCount: Int = 0,
    val pendingTasksCount: Int = 0,
    val lateTasksCount: Int = 0,
    val latestTasks: List<Task> = emptyList(),
    val latestReports: List<TaskReportContext> = emptyList(),
    val teamPreview: List<TeamMemberWithStats> = emptyList(),
    val unreadNotificationsCount: Int = 0
)
