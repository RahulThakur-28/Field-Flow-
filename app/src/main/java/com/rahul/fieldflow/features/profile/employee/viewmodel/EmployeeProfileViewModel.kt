package com.rahul.fieldflow.features.profile.employee.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.fieldflow.domain.model.AppTheme
import com.rahul.fieldflow.domain.repository.AuthRepository
import com.rahul.fieldflow.domain.repository.SettingsRepository
import com.rahul.fieldflow.domain.usecase.home.GetEmployeeHomeDashboardUseCase
import com.rahul.fieldflow.features.profile.employee.state.EmployeeProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmployeeProfileViewModel @Inject constructor(
    private val getEmployeeHomeDashboardUseCase: GetEmployeeHomeDashboardUseCase,
    private val authRepository: AuthRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(EmployeeProfileUiState())
    val uiState: StateFlow<EmployeeProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
        observeSettings()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getEmployeeHomeDashboardUseCase()
                .onSuccess { dashboard ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            userName = dashboard.profile.fullName,
                            initials = dashboard.profile.fullName.take(1) + (dashboard.profile.fullName.split(" ").getOrNull(1)?.take(1) ?: ""),
                            role = "Field Employee",
                            email = dashboard.profile.email,
                            phone = dashboard.profile.phone ?: "",
                            company = "FieldFlow", // Fallback, would be better to get workspace name
                            completedTasks = dashboard.taskStats.completedCount,
                            activeTasks = dashboard.taskStats.activeCount,
                            onTimePercentage = 0 // Calculate if needed
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
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
}
