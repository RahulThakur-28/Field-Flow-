package com.rahul.fieldflow.features.profile.employee.viewmodel

import androidx.lifecycle.ViewModel
import com.rahul.fieldflow.features.profile.employee.state.EmployeeProfileUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EmployeeProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(EmployeeProfileUiState())
    val uiState: StateFlow<EmployeeProfileUiState> = _uiState.asStateFlow()

    fun signOut() {
        // Handle sign out logic
    }
}
