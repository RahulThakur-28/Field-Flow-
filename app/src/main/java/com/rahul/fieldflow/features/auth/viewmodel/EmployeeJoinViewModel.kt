package com.rahul.fieldflow.features.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.fieldflow.domain.repository.AuthRepository
import com.rahul.fieldflow.domain.repository.JoinRequestRepository
import com.rahul.fieldflow.domain.usecase.requests.SubmitJoinRequestUseCase
import com.rahul.fieldflow.domain.usecase.workspace.FindWorkspaceByCodeUseCase
import com.rahul.fieldflow.features.auth.state.EmployeeJoinUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmployeeJoinViewModel @Inject constructor(
    private val findWorkspaceByCodeUseCase: FindWorkspaceByCodeUseCase,
    private val submitJoinRequestUseCase: SubmitJoinRequestUseCase,
    private val authRepository: AuthRepository,
    private val joinRequestRepository: JoinRequestRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmployeeJoinUiState())
    val uiState: StateFlow<EmployeeJoinUiState> = _uiState.asStateFlow()

    init {
        loadEmployeeProfile()
    }

    private fun loadEmployeeProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingProfile = true, error = null) }
            val user = authRepository.currentUser.first()
            if (user != null) {
                _uiState.update {
                    it.copy(
                        fullName = user.fullName,
                        email = user.email,
                        phone = user.phone ?: "",
                        isLoadingProfile = false
                    )
                }
            } else {
                // Try to fetch session if not in flow
                authRepository.getCurrentSession()?.let { userId ->
                    authRepository.getProfile(userId).onSuccess { profile ->
                        _uiState.update {
                            it.copy(
                                fullName = profile.fullName,
                                email = profile.email,
                                phone = profile.phone ?: "",
                                isLoadingProfile = false
                            )
                        }
                    }.onFailure {
                        _uiState.update { it.copy(isLoadingProfile = false, error = "Failed to load profile") }
                    }
                } ?: run {
                    _uiState.update { it.copy(isLoadingProfile = false, error = "User not authenticated") }
                }
            }
        }
    }

    fun onCompanyIdChange(value: String) {
        if (value.length <= 8) {
            _uiState.update { it.copy(companyId = value, companyIdError = null) }
        }
    }

    fun findCompany() {
        val code = _uiState.value.companyId
        if (code.length == 8) {
            viewModelScope.launch {
                _uiState.update { it.copy(isSearching = true, error = null) }
                findWorkspaceByCodeUseCase(code)
                    .onSuccess { workspace ->
                        if (workspace != null) {
                            _uiState.update {
                                it.copy(
                                    isSearching = false,
                                    companyFound = true,
                                    foundCompanyName = workspace.name,
                                    foundCompanyId = workspace.companyIdDisplay,
                                    workspaceId = workspace.id // Need to add this to state
                                )
                            }
                        } else {
                            _uiState.update { it.copy(isSearching = false, companyIdError = "Workspace not found") }
                        }
                    }
                    .onFailure { error ->
                        _uiState.update { it.copy(isSearching = false, error = error.message) }
                    }
            }
        } else {
            _uiState.update { it.copy(companyIdError = "Please enter exactly 8 digits") }
        }
    }

    fun onFullNameChange(value: String) {
        _uiState.update { it.copy(fullName = value, fullNameError = null) }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, emailError = null) }
    }

    fun onPhoneChange(value: String) {
        _uiState.update { it.copy(phone = value) }
    }

    fun sendJoinRequest() {
        val state = _uiState.value
        val workspaceId = state.workspaceId ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null) }
            
            // Check for existing pending request
            joinRequestRepository.getMyRequests().onSuccess { requests ->
                val alreadyPending = requests.any { 
                    it.workspaceId == workspaceId && 
                    it.status == com.rahul.fieldflow.domain.model.JoinRequestStatus.PENDING 
                }
                
                if (alreadyPending) {
                    _uiState.update { it.copy(isSubmitting = false, error = "Request already sent and is pending approval.") }
                    return@onSuccess
                }

                // Proceed to submit
                submitJoinRequestUseCase(workspaceId)
                    .onSuccess {
                        _uiState.update { it.copy(isSubmitting = false, requestSent = true) }
                    }
                    .onFailure { error ->
                        _uiState.update { it.copy(isSubmitting = false, error = error.message) }
                    }
            }.onFailure { error ->
                _uiState.update { it.copy(isSubmitting = false, error = "Failed to check existing requests: ${error.message}") }
            }
        }
    }
    
    fun resetState() {
        _uiState.value = EmployeeJoinUiState()
    }
}
