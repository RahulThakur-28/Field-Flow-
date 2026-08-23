package com.rahul.fieldflow.features.tasks.employee.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.fieldflow.features.tasks.employee.state.EmployeeTaskDetailsUiState
import com.rahul.fieldflow.features.tasks.employee.state.EmployeeTasksUiState
import com.rahul.fieldflow.features.tasks.model.TaskStatus
import com.rahul.fieldflow.features.tasks.model.mockTasks
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EmployeeTasksViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(EmployeeTasksUiState())
    val uiState: StateFlow<EmployeeTasksUiState> = _uiState.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(1000) // Simulate network delay
            
            // In a real app, we would filter by the current employee's ID
            // For now, we use all mock tasks and filter by status for tabs
            _uiState.update { it.copy(tasks = mockTasks, isLoading = false) }
        }
    }

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }
}

class EmployeeTaskDetailsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(EmployeeTaskDetailsUiState())
    val uiState: StateFlow<EmployeeTaskDetailsUiState> = _uiState.asStateFlow()

    fun loadTask(taskId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(800)
            val task = mockTasks.find { it.id == taskId }
            _uiState.update { it.copy(task = task, isLoading = false) }
        }
    }
}
