package com.rahul.fieldflow.features.tasks.owner.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.fieldflow.domain.repository.TaskRepository
import com.rahul.fieldflow.features.tasks.model.Employee
import com.rahul.fieldflow.features.tasks.owner.state.OwnerTasksUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OwnerTasksViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(OwnerTasksUiState())
    val uiState: StateFlow<OwnerTasksUiState> = _uiState.asStateFlow()

    init {
        loadTasks()
    }

    fun loadTasks() {
        viewModelScope.launch {
            Log.d("TASK_FETCH_DEBUG", "Loading owner tasks...")

            _uiState.update { it.copy(isLoading = true) }

            taskRepository.getOwnerTasks()
                .onSuccess { tasks ->

                    Log.d(
                        "TASK_FETCH_DEBUG",
                        "Fetched ${tasks.size} tasks"
                    )

                    tasks.forEach { task ->
                        Log.d(
                            "TASK_FETCH_DEBUG",
                            "Task: id=${task.id}, title=${task.title}"
                        )
                    }

                    val uiTasks = tasks.map { it.toUiTask() }

                    _uiState.update {
                        it.copy(
                            tasks = uiTasks,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                .onFailure { error ->

                    Log.e(
                        "TASK_FETCH_DEBUG",
                        "Failed to fetch owner tasks",
                        error
                    )

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
                }
        }
    }

    private fun com.rahul.fieldflow.domain.model.Task.toUiTask(): com.rahul.fieldflow.features.tasks.model.Task {
        return com.rahul.fieldflow.features.tasks.model.Task(
            id = id,
            title = title,
            description = description ?: "",
            status = when(status) {
                com.rahul.fieldflow.domain.model.TaskStatus.PENDING -> com.rahul.fieldflow.features.tasks.model.TaskStatus.PENDING
                com.rahul.fieldflow.domain.model.TaskStatus.ASSIGNED -> com.rahul.fieldflow.features.tasks.model.TaskStatus.PENDING
                com.rahul.fieldflow.domain.model.TaskStatus.IN_PROGRESS -> com.rahul.fieldflow.features.tasks.model.TaskStatus.IN_PROGRESS
                com.rahul.fieldflow.domain.model.TaskStatus.COMPLETED -> com.rahul.fieldflow.features.tasks.model.TaskStatus.COMPLETED
                com.rahul.fieldflow.domain.model.TaskStatus.CANCELLED -> com.rahul.fieldflow.features.tasks.model.TaskStatus.CANCELLED
                else -> com.rahul.fieldflow.features.tasks.model.TaskStatus.PENDING
            },
            priority = when(priority) {
                com.rahul.fieldflow.domain.model.TaskPriority.LOW -> com.rahul.fieldflow.features.tasks.model.TaskPriority.LOW
                com.rahul.fieldflow.domain.model.TaskPriority.MEDIUM -> com.rahul.fieldflow.features.tasks.model.TaskPriority.MEDIUM
                com.rahul.fieldflow.domain.model.TaskPriority.HIGH -> com.rahul.fieldflow.features.tasks.model.TaskPriority.HIGH
                com.rahul.fieldflow.domain.model.TaskPriority.URGENT -> com.rahul.fieldflow.features.tasks.model.TaskPriority.URGENT
            },
            assignedTo = assignedEmployee?.let { 
                Employee(it.id, it.fullName, "Employee", it.avatarUrl, it.employeeCode) 
            } ?: Employee("", "Unassigned", "Employee"),
            location = location ?: "No location",
            scheduledDate = dueDate ?: createdAt
        )
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }
}
