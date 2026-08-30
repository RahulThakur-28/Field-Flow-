package com.rahul.fieldflow.features.home.employee.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.fieldflow.domain.usecase.home.GetEmployeeHomeDashboardUseCase
import com.rahul.fieldflow.features.home.employee.state.EmployeeHomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmployeeHomeViewModel @Inject constructor(
    private val getEmployeeHomeDashboardUseCase: GetEmployeeHomeDashboardUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmployeeHomeUiState())
    val uiState: StateFlow<EmployeeHomeUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            getEmployeeHomeDashboardUseCase()
                .onSuccess { dashboard ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            userName = dashboard.profile.fullName.split(" ").firstOrNull() ?: dashboard.profile.fullName,
                            initials = dashboard.profile.fullName.take(1) + (dashboard.profile.fullName.split(" ").getOrNull(1)?.take(1) ?: ""),
                            allTasksCount = dashboard.taskStats.totalCount,
                            activeTasksCount = dashboard.taskStats.activeCount,
                            completedTasksCount = dashboard.taskStats.completedCount,
                            lateTasksCount = dashboard.taskStats.lateCount,
                            nextTask = dashboard.nextTask,
                            upcomingTasks = dashboard.upcomingTasks,
                            recentReports = dashboard.recentReports,
                            unreadNotificationsCount = dashboard.unreadNotificationsCount
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    fun refresh() {
        loadDashboardData()
    }
}
