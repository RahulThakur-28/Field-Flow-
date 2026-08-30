package com.rahul.fieldflow.features.home.employee.state

import com.rahul.fieldflow.domain.model.*

data class EmployeeHomeUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val userName: String = "",
    val initials: String = "",
    val allTasksCount: Int = 0,
    val activeTasksCount: Int = 0,
    val completedTasksCount: Int = 0,
    val lateTasksCount: Int = 0,
    val nextTask: Task? = null,
    val upcomingTasks: List<Task> = emptyList(),
    val recentReports: List<TaskReportContext> = emptyList(),
    val unreadNotificationsCount: Int = 0
)
