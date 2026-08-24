package com.rahul.fieldflow.features.auth.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.fieldflow.domain.usecase.auth.LoginUseCase
import com.rahul.fieldflow.features.auth.state.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null) }
    }

    fun login() {
        val email = _uiState.value.email
        val password = _uiState.value.password

        var hasError = false

        if (email.isBlank()) {
            _uiState.update { it.copy(emailError = "Email is required") }
            hasError = true
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.update { it.copy(emailError = "Invalid email format") }
            hasError = true
        }

        if (password.isBlank()) {
            _uiState.update { it.copy(passwordError = "Password is required") }
            hasError = true
        }

        if (!hasError) {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, error = null) }
                loginUseCase(email, password)
                    .onSuccess {
                        _uiState.update { it.copy(isLoading = false, loginSuccess = true) }
                    }
                    .onFailure { error ->
                        _uiState.update { it.copy(isLoading = false, error = error.message) }
                    }
            }
        }
    }
    
    fun resetSuccess() {
        _uiState.update { it.copy(loginSuccess = false) }
    }
}
