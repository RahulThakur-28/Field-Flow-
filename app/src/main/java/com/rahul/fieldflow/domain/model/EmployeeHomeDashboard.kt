package com.rahul.fieldflow.domain.model

data class EmployeeHomeDashboard(
    val profile: UserProfile,
    val workspace: Workspace,
    val taskStats: TaskStats,
    val nextTask: Task?,
    val upcomingTasks: List<Task>,
    val recentReports: List<TaskReportContext>,
    val unreadNotificationsCount: Int
)
