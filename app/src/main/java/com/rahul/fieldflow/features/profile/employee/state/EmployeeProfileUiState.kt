package com.rahul.fieldflow.features.profile.employee.state

import com.rahul.fieldflow.domain.model.AppTheme

data class EmployeeProfileUiState(
    val userName: String = "",
    val initials: String = "",
    val role: String = "Field Employee",
    val company: String = "",
    val email: String = "",
    val phone: String = "",
    val completedTasks: Int = 0,
    val onTimePercentage: Int = 0,
    val activeTasks: Int = 0,
    val appTheme: AppTheme = AppTheme.SYSTEM,
    val isLoading: Boolean = false,
    val error: String? = null
)
