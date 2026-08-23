package com.rahul.fieldflow.features.profile.state

data class OwnerProfileUiState(
    val userName: String = "Rahul Verma",
    val initials: String = "RV",
    val role: String = "Owner",
    val company: String = "FieldFlow Inc.",
    val email: String = "rahul@fieldflow.in",
    val phone: String = "+91 98765 43210",
    val totalTasks: Int = 103,
    val teamSize: Int = 4,
    val efficiency: Int = 91,
    val isLoading: Boolean = false,
    val error: String? = null,
    val pushNotificationsEnabled: Boolean = true,
    val emailNotificationsEnabled: Boolean = true,
    val taskUpdatesEnabled: Boolean = true,
    val teamActivityEnabled: Boolean = true,
    val reportNotificationsEnabled: Boolean = true
)
