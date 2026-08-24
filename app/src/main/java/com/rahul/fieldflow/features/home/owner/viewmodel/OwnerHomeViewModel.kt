package com.rahul.fieldflow.features.home.owner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.fieldflow.domain.usecase.auth.GetCurrentUserUseCase
import com.rahul.fieldflow.domain.usecase.requests.GetPendingRequestsUseCase
import com.rahul.fieldflow.domain.usecase.requests.RespondToJoinRequestUseCase
import com.rahul.fieldflow.domain.usecase.workspace.GetWorkspaceByOwnerUseCase
import com.rahul.fieldflow.features.home.model.dummyOwnerHomeUiState
import com.rahul.fieldflow.features.home.owner.state.OwnerHomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OwnerHomeViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getWorkspaceByOwnerUseCase: GetWorkspaceByOwnerUseCase,
    private val getPendingRequestsUseCase: GetPendingRequestsUseCase,
    private val respondToJoinRequestUseCase: RespondToJoinRequestUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(OwnerHomeUiState())
    val uiState: StateFlow<OwnerHomeUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val user = getCurrentUserUseCase().first()
            if (user != null) {
                _uiState.update { it.copy(
                    userName = user.fullName,
                    initials = user.fullName.take(2).uppercase()
                ) }

                getWorkspaceByOwnerUseCase(user.id).onSuccess { workspace ->
                    _uiState.update { it.copy(companyId = workspace.companyIdDisplay) }
                    loadPendingRequests(workspace.id)
                }
            }

            // Still use some dummy data for other fields for now
            val mockData = dummyOwnerHomeUiState()
            _uiState.update { 
                it.copy(
                    isLoading = false,
                    location = mockData.location,
                    notificationCount = 2,
                    stats = mockData.stats,
                    liveVisits = mockData.liveVisits,
                    teamStatus = mockData.teamStatus,
                    recentActivity = mockData.recentActivity,
                    currentStep = 2
                ) 
            }
        }
    }

    private fun loadPendingRequests(workspaceId: String) {
        viewModelScope.launch {
            getPendingRequestsUseCase(workspaceId).onSuccess { requests ->
                _uiState.update { it.copy(pendingRequests = requests) }
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
