package com.rahul.fieldflow.features.home.model

import androidx.compose.ui.graphics.vector.ImageVector

// Shared Models
data class SummaryStatUiModel(
    val value: String,
    val label: String,
    val type: StatusBadgeType = StatusBadgeType.NEUTRAL
)

enum class StatusBadgeType {
    ACTIVE, DONE, PENDING, LATE, IN_PROGRESS, TRAVELING, IDLE, NEUTRAL, SUCCESS, WARNING
}

// Owner Models
data class FieldVisitUiModel(
    val title: String,
    val employeeName: String,
    val employeeInitials: String,
    val status: StatusBadgeType,
    val location: String,
    val distance: String,
    val completedTasks: Int,
    val totalTasks: Int
)

data class TeamMemberUiModel(
    val name: String,
    val taskName: String,
    val status: StatusBadgeType,
    val initials: String
)

data class ActivityItemUiModel(
    val title: String,
    val time: String,
    val type: ActivityType = ActivityType.INFO
)

enum class ActivityType {
    ARRIVAL, START, PROGRESS, UPLOAD, SUBMISSION, AI, INFO
}

// Employee Models
data class NextTaskUiModel(
    val title: String,
    val scheduledTime: String,
    val status: StatusBadgeType,
    val location: String,
    val distance: String,
    val eta: String,
    val taskCount: Int,
    val dueTime: String,
    val scheduleStatus: String
)

data class ScheduleTaskUiModel(
    val title: String,
    val time: String,
    val location: String,
    val status: StatusBadgeType
)

data class QuickAccessUiModel(
    val title: String,
    val subtitle: String,
    val icon: ImageVector
)
