package com.rahul.fieldflow.features.tasks.owner.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.rahul.fieldflow.core.navigation.AppRoutes
import com.rahul.fieldflow.domain.repository.TaskRepository
import com.rahul.fieldflow.features.tasks.model.Employee
import com.rahul.fieldflow.features.tasks.owner.state.OwnerTasksUiState
import com.rahul.fieldflow.features.tasks.owner.state.TaskFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.time.OffsetDateTime

@HiltViewModel
class OwnerTasksViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val initialFilter = try {
        savedStateHandle.toRoute<AppRoutes.OwnerTasks>().filter?.let { 
            TaskFilter.valueOf(it.uppercase()) 
        } ?: TaskFilter.ALL
    } catch (e: Exception) {
        TaskFilter.ALL
    }

    private val _uiState = MutableStateFlow(OwnerTasksUiState(selectedFilter = initialFilter))
    val uiState: StateFlow<OwnerTasksUiState> = _uiState.asStateFlow()

    init {
        loadTasks()
    }

    fun loadTasks() {
        viewModelScope.launch {
            Log.d("OWNER_TASK_TRACE", "loadTasks: started")
            _uiState.update { it.copy(isLoading = true, error = null) }

            taskRepository.getOwnerTasks()
                .onSuccess { tasks ->
                    val uiTasks = tasks.map { it.toUiTask() }
                    _uiState.update {
                        it.copy(
                            tasks = uiTasks,
                            isLoading = false
                        )
                    }
                    applyFilters()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load tasks"
                        )
                    }
                }
        }
    }

    fun refresh() {
        loadTasks()
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
        val allTasks = state.tasks
        
        val allCount = allTasks.size
        val activeCount = allTasks.count { 
            it.status == com.rahul.fieldflow.features.tasks.model.TaskStatus.PENDING || 
            it.status == com.rahul.fieldflow.features.tasks.model.TaskStatus.IN_PROGRESS 
        }
        val completedCount = allTasks.count { it.status == com.rahul.fieldflow.features.tasks.model.TaskStatus.COMPLETED }
        val overdueCount = allTasks.count { it.status == com.rahul.fieldflow.features.tasks.model.TaskStatus.OVERDUE }

        val filtered = allTasks.filter { task ->
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
        
        _uiState.update { 
            it.copy(
                filteredTasks = filtered,
                allCount = allCount,
                activeCount = activeCount,
                completedCount = completedCount,
                overdueCount = overdueCount
            ) 
        }
    }

    private fun com.rahul.fieldflow.domain.model.Task.toUiTask(): com.rahul.fieldflow.features.tasks.model.Task {
        val uiStatus = if (status != com.rahul.fieldflow.domain.model.TaskStatus.COMPLETED && 
            dueDate?.isBefore(OffsetDateTime.now()) == true) {
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
