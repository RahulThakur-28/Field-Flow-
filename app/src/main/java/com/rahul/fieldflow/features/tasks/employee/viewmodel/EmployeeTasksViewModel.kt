package com.rahul.fieldflow.features.tasks.employee.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.fieldflow.features.tasks.employee.state.EmployeeTasksUiState
import com.rahul.fieldflow.features.tasks.model.mockTasks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmployeeTasksViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(EmployeeTasksUiState())
    val uiState: StateFlow<EmployeeTasksUiState> = _uiState.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(1000)
            _uiState.update { it.copy(tasks = mockTasks, isLoading = false) }
        }
    }

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }
}
