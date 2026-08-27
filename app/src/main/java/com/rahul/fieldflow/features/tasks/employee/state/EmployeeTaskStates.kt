package com.rahul.fieldflow.features.tasks.employee.state

import com.rahul.fieldflow.features.tasks.model.Task

data class EmployeeTasksUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val selectedTab: Int = 1 // 0: Upcoming, 1: Active, 2: Completed
)

data class EmployeeTaskDetailsUiState(
    val task: Task? = null,
    val isLoading: Boolean = false,
    val isInsideGeofence: Boolean = false,
    val distanceToDestination: Float? = null,
    val isTrackingActive: Boolean = false,
    val error: String? = null
)
