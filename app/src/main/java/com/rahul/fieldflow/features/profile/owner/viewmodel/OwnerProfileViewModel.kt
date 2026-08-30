package com.rahul.fieldflow.features.profile.owner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.fieldflow.domain.model.AppTheme
import com.rahul.fieldflow.domain.repository.AuthRepository
import com.rahul.fieldflow.domain.repository.SettingsRepository
import com.rahul.fieldflow.domain.usecase.home.GetOwnerHomeDashboardUseCase
import com.rahul.fieldflow.domain.usecase.profile.UpdateProfileUseCase
import com.rahul.fieldflow.features.profile.owner.state.OwnerProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OwnerProfileViewModel @Inject constructor(
    private val getOwnerHomeDashboardUseCase: GetOwnerHomeDashboardUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val authRepository: AuthRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(OwnerProfileUiState())
    val uiState: StateFlow<OwnerProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
        observeSettings()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getOwnerHomeDashboardUseCase()
                .onSuccess { dashboard ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            userName = dashboard.profile.fullName,
                            initials = dashboard.profile.fullName.take(1) + (dashboard.profile.fullName.split(" ").getOrNull(1)?.take(1) ?: ""),
                            role = "Owner",
                            email = dashboard.profile.email,
                            phone = dashboard.profile.phone,
                            company = dashboard.workspace.name,
                            totalTasks = dashboard.taskStats.totalCount,
                            teamSize = dashboard.totalEmployeesCount
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }
    
    fun reload() {
        loadProfile()
    }

    fun saveProfile(name: String, phone: String?, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            updateProfileUseCase(name, phone)
                .onSuccess {
                    loadProfile()
                    onResult(true)
                }
                .onFailure {
                    onResult(false)
                }
        }
    }

    private fun observeSettings() {
        settingsRepository.theme
            .onEach { theme ->
                _uiState.update { it.copy(appTheme = theme) }
            }
            .launchIn(viewModelScope)
    }

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            settingsRepository.setTheme(theme)
        }
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
