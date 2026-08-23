package com.rahul.fieldflow.features.profile.owner.viewmodel

import androidx.lifecycle.ViewModel
import com.rahul.fieldflow.features.profile.owner.state.OwnerProfileUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class OwnerProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OwnerProfileUiState())
    val uiState: StateFlow<OwnerProfileUiState> = _uiState.asStateFlow()

    fun updateName(name: String) {
        _uiState.update { it.copy(userName = name, initials = name.take(2).uppercase()) }
    }

    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun updatePhone(phone: String) {
        _uiState.update { it.copy(phone = phone) }
    }

    fun updateCompany(company: String) {
        _uiState.update { it.copy(company = company) }
    }

    fun togglePushNotifications(enabled: Boolean) {
        _uiState.update { it.copy(pushNotificationsEnabled = enabled) }
    }

    fun toggleEmailNotifications(enabled: Boolean) {
        _uiState.update { it.copy(emailNotificationsEnabled = enabled) }
    }

    fun toggleTaskUpdates(enabled: Boolean) {
        _uiState.update { it.copy(taskUpdatesEnabled = enabled) }
    }

    fun toggleTeamActivity(enabled: Boolean) {
        _uiState.update { it.copy(teamActivityEnabled = enabled) }
    }

    fun toggleReportNotifications(enabled: Boolean) {
        _uiState.update { it.copy(reportNotificationsEnabled = enabled) }
    }
}
