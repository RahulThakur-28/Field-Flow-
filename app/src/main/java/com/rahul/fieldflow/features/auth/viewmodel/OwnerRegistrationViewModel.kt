package com.rahul.fieldflow.features.auth.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.fieldflow.domain.model.UserRole
import com.rahul.fieldflow.domain.usecase.auth.RegisterUseCase
import com.rahul.fieldflow.features.auth.state.OwnerRegistrationUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OwnerRegistrationViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OwnerRegistrationUiState())
    val uiState: StateFlow<OwnerRegistrationUiState> = _uiState.asStateFlow()

    fun onFullNameChange(value: String) {
        _uiState.update { it.copy(fullName = value, fullNameError = null) }
    }

    fun onCompanyNameChange(value: String) {
        _uiState.update { it.copy(companyName = value, companyNameError = null) }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, emailError = null) }
    }

    fun onPhoneChange(value: String) {
        _uiState.update { it.copy(phone = value) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null) }
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.update { it.copy(confirmPassword = value, confirmPasswordError = null) }
    }

    fun register() {
        val state = _uiState.value
        var hasError = false

        if (state.fullName.isBlank()) {
            _uiState.update { it.copy(fullNameError = "Full name is required") }
            hasError = true
        }

        if (state.companyName.isBlank()) {
            _uiState.update { it.copy(companyNameError = "Company name is required") }
            hasError = true
        }

        if (state.email.isBlank()) {
            _uiState.update { it.copy(emailError = "Email is required") }
            hasError = true
        } else if (!Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            _uiState.update { it.copy(emailError = "Invalid email format") }
            hasError = true
        }

        if (state.password.length < 6) {
            _uiState.update { it.copy(passwordError = "Password must be at least 6 characters") }
            hasError = true
        }

        if (state.confirmPassword != state.password) {
            _uiState.update { it.copy(confirmPasswordError = "Passwords do not match") }
            hasError = true
        }

        if (!hasError) {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, error = null) }
                registerUseCase(
                    email = state.email,
                    password = state.password,
                    fullName = state.fullName,
                    role = UserRole.OWNER,
                    phone = state.phone,
                    companyName = state.companyName
                ).onSuccess {
                    _uiState.update { it.copy(isLoading = false, registrationSuccess = true) }
                }.onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            }
        }
    }
    
    fun resetSuccess() {
        _uiState.update { it.copy(registrationSuccess = false) }
    }
}
