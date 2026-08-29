package com.rahul.fieldflow.features.profile.owner.state

import com.rahul.fieldflow.domain.model.AppTheme

data class OwnerProfileUiState(
    val userName: String = "",
    val initials: String = "",
    val role: String = "Owner",
    val company: String = "",
    val email: String = "",
    val phone: String? = null,
    val totalTasks: Int = 0,
    val teamSize: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val appTheme: AppTheme = AppTheme.SYSTEM,
    val pushNotificationsEnabled: Boolean = true,
    val emailNotificationsEnabled: Boolean = true,
    val taskUpdatesEnabled: Boolean = true,
    val teamActivityEnabled: Boolean = true,
    val reportNotificationsEnabled: Boolean = true
)
