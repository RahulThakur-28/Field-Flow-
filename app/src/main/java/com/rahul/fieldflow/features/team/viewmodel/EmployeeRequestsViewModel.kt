package com.rahul.fieldflow.features.team.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.fieldflow.domain.repository.AuthRepository
import com.rahul.fieldflow.domain.repository.JoinRequestRepository
import com.rahul.fieldflow.features.team.state.EmployeeRequestsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmployeeRequestsViewModel @Inject constructor(
    private val joinRequestRepository: JoinRequestRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmployeeRequestsUiState())
    val uiState: StateFlow<EmployeeRequestsUiState> = _uiState.asStateFlow()

    init {
        loadRequests()
    }

    fun loadRequests() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val user = authRepository.currentUser.first()
            val workspaceId = user?.workspaceId
            
            if (workspaceId != null) {
                joinRequestRepository.getPendingRequests(workspaceId)
                    .onSuccess { requests ->
                        _uiState.update { it.copy(requests = requests, isLoading = false) }
                    }
                    .onFailure { error ->
                        _uiState.update { it.copy(isLoading = false, error = error.message) }
                    }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Workspace not found") }
            }
        }
    }

    fun approveRequest(requestId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(processingRequestId = requestId, error = null) }
            joinRequestRepository.respondToRequest(requestId, true)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(
                            requests = state.requests.filter { it.id != requestId },
                            processingRequestId = null
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(processingRequestId = null, error = error.message) }
                }
        }
    }

    fun rejectRequest(requestId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(processingRequestId = requestId, error = null) }
            joinRequestRepository.respondToRequest(requestId, false)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(
                            requests = state.requests.filter { it.id != requestId },
                            processingRequestId = null
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(processingRequestId = null, error = error.message) }
                }
        }
    }
}
