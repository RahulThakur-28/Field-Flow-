package com.rahul.fieldflow.features.home.employee.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.fieldflow.features.home.employee.state.EmployeeHomeUiState
import com.rahul.fieldflow.features.home.model.dummyEmployeeHomeUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EmployeeHomeViewModel : ViewModel() {

    private val mockData = dummyEmployeeHomeUiState()

    private val _uiState = MutableStateFlow(
        EmployeeHomeUiState(
            isLoading = false,
            userName = mockData.userName,
            date = "Friday, Aug 22",
            initials = "RT",
            notificationCount = 1,
            stats = mockData.stats,
            nextTask = mockData.nextTask,
            schedule = mockData.schedule,
            quickAccess = mockData.quickAccess
        )
    )
    val uiState: StateFlow<EmployeeHomeUiState> = _uiState.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(1500) // Simulate network delay
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}
