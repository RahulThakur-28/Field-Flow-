package com.rahul.fieldflow.features.auth.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.fieldflow.features.auth.state.EmployeeJoinUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EmployeeJoinViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EmployeeJoinUiState())
    val uiState: StateFlow<EmployeeJoinUiState> = _uiState.asStateFlow()

    fun onCompanyIdChange(value: String) {
        if (value.length <= 8) {
            _uiState.update { it.copy(companyId = value, companyIdError = null) }
        }
    }

    fun findCompany() {
        if (_uiState.value.companyId.length == 8) {
            viewModelScope.launch {
                _uiState.update { it.copy(isSearching = true, error = null) }
                delay(1000)
                _uiState.update { it.copy(isSearching = false, companyFound = true) }
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
        var hasError = false

        if (state.fullName.isBlank()) {
            _uiState.update { it.copy(fullNameError = "Full name is required") }
            hasError = true
        }

        if (state.email.isBlank()) {
            _uiState.update { it.copy(emailError = "Email is required") }
            hasError = true
        } else if (!Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            _uiState.update { it.copy(emailError = "Invalid email format") }
            hasError = true
        }

        if (!hasError) {
            viewModelScope.launch {
                _uiState.update { it.copy(isSubmitting = true, error = null) }
                delay(1500)
                _uiState.update { it.copy(isSubmitting = false, requestSent = true) }
            }
        }
    }
    
    fun resetState() {
        _uiState.value = EmployeeJoinUiState()
    }
}
