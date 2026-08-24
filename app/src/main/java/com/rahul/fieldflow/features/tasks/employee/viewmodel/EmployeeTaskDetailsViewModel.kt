package com.rahul.fieldflow.features.tasks.employee.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.fieldflow.features.tasks.employee.state.EmployeeTaskDetailsUiState
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
class EmployeeTaskDetailsViewModel @Inject constructor() : ViewModel() {
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
