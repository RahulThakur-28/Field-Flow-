package com.rahul.fieldflow.features.profile.employee.state

data class EmployeeProfileUiState(
    val userName: String = "Rahul Thakur",
    val initials: String = "RT",
    val role: String = "Field Employee",
    val company: String = "FieldFlow Inc.",
    val email: String = "rahul.thakur@fieldflow.in",
    val phone: String = "+91 98765 43210",
    val completedTasks: Int = 24,
    val onTimePercentage: Int = 92,
    val activeTasks: Int = 2,
    val isLoading: Boolean = false,
    val error: String? = null
)
