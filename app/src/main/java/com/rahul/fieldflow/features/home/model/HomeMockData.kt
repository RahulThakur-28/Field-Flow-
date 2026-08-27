package com.rahul.fieldflow.features.home.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Description
import com.rahul.fieldflow.features.home.employee.state.EmployeeHomeUiState
import com.rahul.fieldflow.features.home.owner.state.OwnerHomeUiState

fun dummyOwnerHomeUiState() = OwnerHomeUiState(
    userName = "Rahul",
    location = "Mumbai",
    stats = listOf(
        SummaryStatUiModel("12", "Active", StatusBadgeType.ACTIVE),
        SummaryStatUiModel("86", "Done", StatusBadgeType.DONE),
        SummaryStatUiModel("14", "Pending", StatusBadgeType.PENDING),
        SummaryStatUiModel("3", "Late", StatusBadgeType.LATE)
    ),
    liveVisits = listOf(
        FieldVisitUiModel(
            title = "College Placement Visit",
            employeeName = "Rahul Thakur",
            employeeInitials = "RT",
            status = StatusBadgeType.IN_PROGRESS,
            location = "ABC College",
            distance = "1.2 km away",
            completedTasks = 3,
            totalTasks = 5
        ),
        FieldVisitUiModel(
            title = "Client Site Inspection",
            employeeName = "Priya Sharma",
            employeeInitials = "PS",
            status = StatusBadgeType.TRAVELING,
            location = "TechPark Phase 2",
            distance = "4.5 km away",
            completedTasks = 0,
            totalTasks = 6
        )
    ),
    teamStatus = listOf(
        TeamMemberUiModel("Rahul Thakur", "College Placement Visit", StatusBadgeType.IN_PROGRESS, "RT"),
        TeamMemberUiModel("Priya Sharma", "Client Site Inspection", StatusBadgeType.TRAVELING, "PS"),
        TeamMemberUiModel("Kavya Nair", "Sales Visit — TechCorp", StatusBadgeType.PENDING, "KN"),
        TeamMemberUiModel("Arjun Mehta", "No active task", StatusBadgeType.IDLE, "AM")
    ),
    recentActivity = listOf(
        ActivityItemUiModel("Rahul arrived at ABC College", "10:42 AM"),
        ActivityItemUiModel("Task started by Rahul", "11:05 AM"),
        ActivityItemUiModel("Checklist: 2/5 items completed", "11:32 AM"),
        ActivityItemUiModel("Photo proof uploaded (3 photos)", "12:10 PM"),
        ActivityItemUiModel("Report submitted", "12:18 PM"),
        ActivityItemUiModel("AI summary generated", "12:22 PM")
    )
)

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
