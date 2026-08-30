package com.rahul.fieldflow.features.tasks.employee.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.rahul.fieldflow.core.navigation.AppRoutes
import com.rahul.fieldflow.domain.usecase.tasks.GetEmployeeTasksUseCase
import com.rahul.fieldflow.features.tasks.employee.state.EmployeeTasksUiState
import com.rahul.fieldflow.features.tasks.model.Employee
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmployeeTasksViewModel @Inject constructor(
    private val getEmployeeTasksUseCase: GetEmployeeTasksUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val initialTab = try {
        savedStateHandle.toRoute<AppRoutes.EmployeeTasks>().filter?.let { filter ->
            when (filter.lowercase()) {
                "active" -> 1
                "completed" -> 2
                "overdue" -> 3
                else -> 0
            }
        } ?: 0
    } catch (e: Exception) {
        0
    }

    private val _uiState = MutableStateFlow(EmployeeTasksUiState(selectedTab = initialTab))
    val uiState: StateFlow<EmployeeTasksUiState> = _uiState.asStateFlow()

    init {
        loadTasks()
    }

    fun loadTasks() {
        viewModelScope.launch {
            Log.d("EMPLOYEE_TASK_TRACE", "loadTasks: started")
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            getEmployeeTasksUseCase()
                .onSuccess { tasks ->
                    Log.d("EMPLOYEE_TASK_TRACE", "loadTasks: Domain Task count = ${tasks.size}")
                    val uiTasks = tasks.map { it.toUiTask() }
                    _uiState.update { 
                        it.copy(
                            tasks = uiTasks, 
                            isLoading = false 
                        ) 
                    }
                }
                .onFailure { error ->
                    Log.e("EMPLOYEE_TASK_TRACE", "loadTasks: failure = ${error.message}")
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = error.message ?: "Failed to load tasks" 
                        ) 
                    }
                }
        }
    }

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    private fun com.rahul.fieldflow.domain.model.Task.toUiTask(): com.rahul.fieldflow.features.tasks.model.Task {
        val uiStatus = if (status != com.rahul.fieldflow.domain.model.TaskStatus.COMPLETED && 
            dueDate?.isBefore(java.time.OffsetDateTime.now()) == true) {
            com.rahul.fieldflow.features.tasks.model.TaskStatus.OVERDUE
        } else {
            when(status) {
                com.rahul.fieldflow.domain.model.TaskStatus.PENDING -> com.rahul.fieldflow.features.tasks.model.TaskStatus.PENDING
                com.rahul.fieldflow.domain.model.TaskStatus.ASSIGNED -> com.rahul.fieldflow.features.tasks.model.TaskStatus.PENDING
                com.rahul.fieldflow.domain.model.TaskStatus.IN_PROGRESS -> com.rahul.fieldflow.features.tasks.model.TaskStatus.IN_PROGRESS
                com.rahul.fieldflow.domain.model.TaskStatus.COMPLETED -> com.rahul.fieldflow.features.tasks.model.TaskStatus.COMPLETED
                com.rahul.fieldflow.domain.model.TaskStatus.CANCELLED -> com.rahul.fieldflow.features.tasks.model.TaskStatus.CANCELLED
            }
        }

        return com.rahul.fieldflow.features.tasks.model.Task(
            id = id,
            title = title,
            description = description ?: "",
            status = uiStatus,
            priority = when(priority) {
                com.rahul.fieldflow.domain.model.TaskPriority.LOW -> com.rahul.fieldflow.features.tasks.model.TaskPriority.LOW
                com.rahul.fieldflow.domain.model.TaskPriority.MEDIUM -> com.rahul.fieldflow.features.tasks.model.TaskPriority.MEDIUM
                com.rahul.fieldflow.domain.model.TaskPriority.HIGH -> com.rahul.fieldflow.features.tasks.model.TaskPriority.HIGH
                com.rahul.fieldflow.domain.model.TaskPriority.URGENT -> com.rahul.fieldflow.features.tasks.model.TaskPriority.URGENT
            },
            assignedTo = assignedEmployee?.let { 
                Employee(it.id, it.fullName, "Employee", it.avatarUrl, it.employeeCode) 
            } ?: Employee("", "Unassigned", "Employee"),
            location = location ?: "Task location",
            latitude = latitude,
            longitude = longitude,
            radiusMeters = radiusMeters,
            scheduledDate = dueDate ?: createdAt
        )
    }
}
