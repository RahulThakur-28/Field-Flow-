package com.rahul.fieldflow.features.home.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Description
import com.rahul.fieldflow.features.home.employee.state.EmployeeHomeUiState

fun dummyEmployeeHomeUiState() = EmployeeHomeUiState(
    userName = "Rahul",
    stats = listOf(
        SummaryStatUiModel("2", "Today's Tasks"),
        SummaryStatUiModel("1", "Completed"),
        SummaryStatUiModel("1", "Pending")
    ),
    nextTask = NextTaskUiModel(
        title = "College Placement Visit",
        scheduledTime = "10:30 AM",
        status = StatusBadgeType.IN_PROGRESS,
        location = "ABC College",
        distance = "1.2 km",
        eta = "8 min",
        taskCount = 5,
        dueTime = "12:00 PM Today",
        scheduleStatus = "On schedule"
    ),
    schedule = listOf(
        ScheduleTaskUiModel("College Placement Visit", "10:30 AM", "ABC College", StatusBadgeType.IN_PROGRESS),
        ScheduleTaskUiModel("Document Collection", "09:00 AM", "HDFC Bank Branch", StatusBadgeType.DONE)
    ),
    quickAccess = listOf(
        QuickAccessUiModel("My Tasks", "1 pending", Icons.Default.Assignment, androidx.compose.ui.graphics.Color(0xFF2196F3)),
        QuickAccessUiModel("Reports", "2 submitted", Icons.Default.Description, androidx.compose.ui.graphics.Color(0xFFFF9800))
    )
)
