package com.rahul.fieldflow.features.tasks.owner.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.fieldflow.domain.repository.AuthRepository
import com.rahul.fieldflow.domain.repository.TaskRepository
import com.rahul.fieldflow.features.tasks.model.Employee
import com.rahul.fieldflow.features.tasks.owner.state.OwnerTasksUiState
import com.rahul.fieldflow.features.tasks.owner.state.TaskFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OwnerTasksViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(OwnerTasksUiState())
    val uiState: StateFlow<OwnerTasksUiState> = _uiState.asStateFlow()

    init {
        loadTasks()
    }

    fun loadTasks() {
        viewModelScope.launch {
            Log.d("OWNER_TASK_TRACE", "loadTasks: started")
            _uiState.update { it.copy(isLoading = true) }

            val currentUser = authRepository.currentUser.first()
            Log.d("OWNER_TASK_TRACE", "loadTasks: authenticatedUserId = ${currentUser?.id}")
            Log.d("OWNER_TASK_TRACE", "loadTasks: owner workspaceId = ${currentUser?.workspaceId}")

            taskRepository.getOwnerTasks()
                .onSuccess { tasks ->
                    Log.d("OWNER_TASK_TRACE", "loadTasks: Domain Task count = ${tasks.size}")
                    val uiTasks = tasks.map { it.toUiTask() }
                    Log.d("OWNER_TASK_TRACE", "loadTasks: Owner UI task count = ${uiTasks.size}")
                    _uiState.update {
                        it.copy(
                            tasks = uiTasks,
                            isLoading = false,
                            error = null
                        )
                    }
                    applyFilters()
                }
                .onFailure { error ->
                    Log.e("OWNER_TASK_TRACE", "loadTasks: exception = ${error.javaClass.simpleName}, message = ${error.message}")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        Log.d("OWNER_TASK_TRACE", "onSearchQueryChange: query = $query")
        _uiState.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    fun onFilterSelected(filter: TaskFilter) {
        Log.d("OWNER_TASK_TRACE", "onFilterSelected: selected filter = $filter")
        _uiState.update { it.copy(selectedFilter = filter) }
        applyFilters()
    }

    private fun applyFilters() {
        val state = _uiState.value
        val filtered = state.tasks.filter { task ->
            val matchesSearch = task.title.contains(state.searchQuery, ignoreCase = true) ||
                    task.assignedTo.name.contains(state.searchQuery, ignoreCase = true) ||
                    task.location.contains(state.searchQuery, ignoreCase = true)

            val matchesFilter = when (state.selectedFilter) {
                TaskFilter.ALL -> true
                TaskFilter.ACTIVE -> {
                    task.status == com.rahul.fieldflow.features.tasks.model.TaskStatus.PENDING ||
                            task.status == com.rahul.fieldflow.features.tasks.model.TaskStatus.IN_PROGRESS
                }
                TaskFilter.COMPLETED -> task.status == com.rahul.fieldflow.features.tasks.model.TaskStatus.COMPLETED
                TaskFilter.OVERDUE -> task.status == com.rahul.fieldflow.features.tasks.model.TaskStatus.OVERDUE
            }

            matchesSearch && matchesFilter
        }
        Log.d("OWNER_TASK_TRACE", "applyFilters: filtered task count = ${filtered.size}")
        _uiState.update { it.copy(filteredTasks = filtered) }
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
            location = location ?: "Task location",
            latitude = latitude,
            longitude = longitude,
            radiusMeters = radiusMeters,
            scheduledDate = dueDate ?: createdAt
        )
    }
}
