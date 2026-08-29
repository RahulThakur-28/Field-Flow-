package com.rahul.fieldflow.features.home.owner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.fieldflow.domain.usecase.home.GetOwnerHomeDashboardUseCase
import com.rahul.fieldflow.domain.usecase.requests.RespondToJoinRequestUseCase
import com.rahul.fieldflow.features.home.owner.state.OwnerHomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OwnerHomeViewModel @Inject constructor(
    private val getOwnerHomeDashboardUseCase: GetOwnerHomeDashboardUseCase,
    private val respondToJoinRequestUseCase: RespondToJoinRequestUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(OwnerHomeUiState())
    val uiState: StateFlow<OwnerHomeUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            getOwnerHomeDashboardUseCase()
                .onSuccess { dashboard ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            userName = dashboard.profile.fullName,
                            initials = dashboard.profile.fullName.take(1) + (dashboard.profile.fullName.split(" ").getOrNull(1)?.take(1) ?: ""),
                            companyName = dashboard.workspace.name,
                            companyId = dashboard.workspace.companyIdDisplay,
                            totalTasksCount = dashboard.taskStats.totalCount,
                            activeTasksCount = dashboard.taskStats.activeCount,
                            completedTasksCount = dashboard.taskStats.completedCount,
                            pendingTasksCount = dashboard.taskStats.pendingCount,
                            lateTasksCount = dashboard.taskStats.lateCount,
                            latestTasks = dashboard.latestTasks,
                            latestReports = dashboard.latestReports,
                            teamPreview = dashboard.teamPreview
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    fun approveRequest(requestId: String) {
        viewModelScope.launch {
            respondToJoinRequestUseCase(requestId, true).onSuccess {
                loadDashboardData() // Refresh
            }
        }
    }

    fun rejectRequest(requestId: String) {
        viewModelScope.launch {
            respondToJoinRequestUseCase(requestId, false).onSuccess {
                loadDashboardData() // Refresh
            }
        }
    }

    fun refresh() {
        loadDashboardData()
    }
}
