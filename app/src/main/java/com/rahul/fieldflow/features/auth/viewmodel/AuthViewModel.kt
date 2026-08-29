package com.rahul.fieldflow.features.auth.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.fieldflow.domain.model.UserProfile
import com.rahul.fieldflow.domain.model.UserRole
import com.rahul.fieldflow.domain.repository.AuthRepository
import com.rahul.fieldflow.domain.usecase.auth.GetCurrentUserUseCase
import com.rahul.fieldflow.domain.usecase.auth.GetSessionUseCase
import com.rahul.fieldflow.domain.usecase.auth.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

sealed class AuthState {
    object Checking : AuthState()
    data class Authenticated(val user: UserProfile) : AuthState()
    object Unauthenticated : AuthState()
    data class EmailUnverified(val user: UserProfile) : AuthState()
    object NoWorkspace : AuthState()
    object PendingApproval : AuthState()
    object Rejected : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val getSessionUseCase: GetSessionUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val authRepository: AuthRepository,
    private val joinRequestRepository: com.rahul.fieldflow.domain.repository.JoinRequestRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Checking)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private var isSessionCheckComplete = false

    init {
        Log.d("FIELD_FLOW_STARTUP", "AuthViewModel initialized")
        observeCurrentUser()
        checkSession()
    }

    private fun observeCurrentUser() {
        Log.d("FIELD_FLOW_STARTUP", "observeCurrentUser started")
        getCurrentUserUseCase()
            .onEach { user ->
                Log.d("FIELD_FLOW_STARTUP", "Current user emission: ${user?.email ?: "null"}")
                if (user != null) {
                    processAuthenticatedUser(user)
                } else {
                    // If session check is done and user is still null, we are definitely unauthenticated
                    if (isSessionCheckComplete) {
                        _authState.value = AuthState.Unauthenticated
                        Log.d("FIELD_FLOW_STARTUP", "AuthState -> Unauthenticated (user null and check complete)")
                    } else {
                        Log.d("FIELD_FLOW_STARTUP", "User null but session check still in progress...")
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private suspend fun processAuthenticatedUser(user: UserProfile) {
        val isVerified = authRepository.isEmailVerified()
        Log.d("FIELD_FLOW_STARTUP", "processAuthenticatedUser for: ${user.email}, isVerified=$isVerified")
        
        if (!isVerified) {
            _authState.value = AuthState.EmailUnverified(user)
            Log.d("FIELD_FLOW_STARTUP", "AuthState -> EmailUnverified")
            return
        }

        if (user.role == UserRole.OWNER) {
            _authState.value = AuthState.Authenticated(user)
            Log.d("FIELD_FLOW_STARTUP", "AuthState -> Authenticated (OWNER)")
            return
        }

        if (user.workspaceId != null) {
            _authState.value = AuthState.Authenticated(user)
            Log.d("FIELD_FLOW_STARTUP", "AuthState -> Authenticated (EMPLOYEE with workspace)")
            return
        }

        // Employee with no workspace_id - check join requests
        Log.d("FIELD_FLOW_STARTUP", "Checking join requests for employee...")
        
        val requestsResult = withTimeoutOrNull(8000) {
            joinRequestRepository.getMyRequests()
        }

        if (requestsResult != null) {
            requestsResult.onSuccess { requests ->
                val pending = requests.any { it.status == com.rahul.fieldflow.domain.model.JoinRequestStatus.PENDING }
                val rejected = requests.any { it.status == com.rahul.fieldflow.domain.model.JoinRequestStatus.REJECTED }

                if (pending) {
                    _authState.value = AuthState.PendingApproval
                    Log.d("FIELD_FLOW_STARTUP", "AuthState -> PendingApproval")
                } else if (rejected) {
                    _authState.value = AuthState.Rejected
                    Log.d("FIELD_FLOW_STARTUP", "AuthState -> Rejected")
                } else {
                    _authState.value = AuthState.NoWorkspace
                    Log.d("FIELD_FLOW_STARTUP", "AuthState -> NoWorkspace")
                }
            }.onFailure {
                Log.e("FIELD_FLOW_STARTUP", "Failed to check join requests", it)
                _authState.value = AuthState.NoWorkspace
                Log.d("FIELD_FLOW_STARTUP", "AuthState -> NoWorkspace (failure recovery)")
            }
        } else {
            Log.e("FIELD_FLOW_STARTUP", "Join requests check timed out")
            _authState.value = AuthState.NoWorkspace
            Log.d("FIELD_FLOW_STARTUP", "AuthState -> NoWorkspace (timeout recovery)")
        }
    }

    private fun checkSession() {
        viewModelScope.launch {
            Log.d("FIELD_FLOW_STARTUP", "checkSession started")
            _authState.value = AuthState.Checking
            
            // Wait if a deep link is being processed
            Log.d("FIELD_FLOW_STARTUP", "Checking if deep link is being processed...")
            authRepository.isProcessingDeepLink
                .filter { isProcessing -> 
                    Log.d("FIELD_FLOW_STARTUP", "isProcessingDeepLink emission: $isProcessing")
                    !isProcessing 
                }
                .first()
            
            Log.d("FIELD_FLOW_STARTUP", "Deep link processing finished or not active, continuing checkSession")

            try {
                val userId = getSessionUseCase()
                Log.d("FIELD_FLOW_STARTUP", "getSessionUseCase returned userId: $userId")
                
                if (userId == null) {
                    isSessionCheckComplete = true
                    _authState.value = AuthState.Unauthenticated
                    Log.d("FIELD_FLOW_STARTUP", "AuthState -> Unauthenticated (userId null)")
                } else {
                    // If we have a userId but user emission is still null, it might mean profile load failed
                    // Give it a bit of time for observeCurrentUser to pick up the profile
                    delay(2000)
                    isSessionCheckComplete = true
                    
                    if (_authState.value is AuthState.Checking) {
                        Log.d("FIELD_FLOW_STARTUP", "Still in Checking after delay, forcing profile reload...")
                        val profileResult = authRepository.getProfile(userId)
                        profileResult.onFailure {
                            Log.e("FIELD_FLOW_STARTUP", "Profile load failed for existing session", it)
                            _authState.value = AuthState.Unauthenticated
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("FIELD_FLOW_STARTUP", "checkSession error", e)
                isSessionCheckComplete = true
                _authState.value = AuthState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            logoutUseCase().onSuccess {
                _authState.value = AuthState.Unauthenticated
                onSuccess()
            }
        }
    }

    fun refreshStatus() {
        viewModelScope.launch {
            Log.d("AUTH_DEBUG", "refreshStatus started")
            _authState.value = AuthState.Checking
            authRepository.refreshProfile()
                .onSuccess { user ->
                    Log.d("AUTH_DEBUG", "refreshProfile success for ${user.email}")
                    processAuthenticatedUser(user)
                }
                .onFailure {
                    Log.e("AUTH_DEBUG", "refreshProfile failed", it)
                    checkSession()
                }
        }
    }

    fun resendVerificationEmail(email: String) {
        viewModelScope.launch {
            authRepository.resendVerificationEmail(email)
        }
    }
}
