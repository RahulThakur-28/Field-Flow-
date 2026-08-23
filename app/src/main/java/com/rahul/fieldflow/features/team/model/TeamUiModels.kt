package com.rahul.fieldflow.features.team.model

import com.rahul.fieldflow.features.tasks.model.Employee
import com.rahul.fieldflow.features.tasks.model.Task
import com.rahul.fieldflow.features.tasks.model.TaskStatus
import com.rahul.fieldflow.features.tasks.model.mockEmployees
import com.rahul.fieldflow.features.tasks.model.mockTasks
import java.time.LocalDateTime

data class EmployeePerformance(
    val completedTasks: Int,
    val onTimePercentage: Int,
    val activeTasks: Int
)

data class EmployeeTeamUiModel(
    val employee: Employee,
    val currentTaskTitle: String? = null,
    val status: String, // "Active" or "Idle"
    val performance: EmployeePerformance
)

data class EmployeeActivity(
    val title: String,
    val timestamp: String
)

val mockTeamMembers = listOf(
    EmployeeTeamUiModel(
        employee = mockEmployees[0],
        currentTaskTitle = "College Placement Visit",
        status = "Active",
        performance = EmployeePerformance(24, 92, 2)
    ),
    EmployeeTeamUiModel(
        employee = mockEmployees[1],
        currentTaskTitle = "Client Site Inspection",
        status = "Active",
        performance = EmployeePerformance(38, 97, 1)
    ),
    EmployeeTeamUiModel(
        employee = mockEmployees[2],
        currentTaskTitle = null,
        status = "Idle",
        performance = EmployeePerformance(17, 85, 0)
    ),
    EmployeeTeamUiModel(
        employee = mockEmployees[3],
        currentTaskTitle = "Service Center Audit",
        status = "Active",
        performance = EmployeePerformance(21, 88, 1)
    )
)

val mockEmployeeActivities = listOf(
    EmployeeActivity("Rahul arrived at ABC College", "10:42 AM"),
    EmployeeActivity("Task started by Rahul", "11:05 AM"),
    EmployeeActivity("Checklist: 2/5 items completed", "11:32 AM"),
    EmployeeActivity("Photo proof uploaded (3 photos)", "12:10 AM")
)
