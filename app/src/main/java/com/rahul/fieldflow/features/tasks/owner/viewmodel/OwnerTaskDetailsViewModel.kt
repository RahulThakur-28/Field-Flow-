package com.rahul.fieldflow.features.tasks.owner.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.fieldflow.domain.repository.TaskRepository
import com.rahul.fieldflow.features.tasks.owner.state.OwnerTaskDetailsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OwnerTaskDetailsViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OwnerTaskDetailsUiState())
    val uiState: StateFlow<OwnerTaskDetailsUiState> = _uiState.asStateFlow()

    fun loadTask(taskId: String) {
        viewModelScope.launch {
            Log.d("TASK_DETAILS_DEBUG", "Loading task: $taskId")

            _uiState.update {
                it.copy(
                    isLoading = true
                )
            }

            taskRepository.getTaskById(taskId)
                .onSuccess { task ->
                    Log.d(
                        "TASK_DETAILS_DEBUG",
                        "Task loaded: id=${task.id}, title=${task.title}"
                    )

                    Log.d(
                        "TASK_DETAILS_DEBUG",
                        "Assigned employee=${task.assignedEmployee?.fullName}"
                    )

                    _uiState.update {
                        it.copy(
                            task = task.toUiTask(),
                            isLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    Log.e(
                        "TASK_DETAILS_DEBUG",
                        "Failed to load task: $taskId",
                        error
                    )

                    _uiState.update {
                        it.copy(
                            task = null,
                            isLoading = false
                        )
                    }
                }
        }
    }

    private fun com.rahul.fieldflow.domain.model.Task.toUiTask():
            com.rahul.fieldflow.features.tasks.model.Task {

        return com.rahul.fieldflow.features.tasks.model.Task(
            id = id,
            title = title,
            description = description ?: "",
            status = when (status) {
                com.rahul.fieldflow.domain.model.TaskStatus.PENDING,
                com.rahul.fieldflow.domain.model.TaskStatus.ASSIGNED ->
                    com.rahul.fieldflow.features.tasks.model.TaskStatus.PENDING

                com.rahul.fieldflow.domain.model.TaskStatus.IN_PROGRESS ->
                    com.rahul.fieldflow.features.tasks.model.TaskStatus.IN_PROGRESS

                com.rahul.fieldflow.domain.model.TaskStatus.COMPLETED ->
                    com.rahul.fieldflow.features.tasks.model.TaskStatus.COMPLETED

                com.rahul.fieldflow.domain.model.TaskStatus.CANCELLED ->
                    com.rahul.fieldflow.features.tasks.model.TaskStatus.CANCELLED

                else ->
                    com.rahul.fieldflow.features.tasks.model.TaskStatus.PENDING
            },
            priority = when (priority) {
                com.rahul.fieldflow.domain.model.TaskPriority.LOW ->
                    com.rahul.fieldflow.features.tasks.model.TaskPriority.LOW

                com.rahul.fieldflow.domain.model.TaskPriority.MEDIUM ->
                    com.rahul.fieldflow.features.tasks.model.TaskPriority.MEDIUM

                com.rahul.fieldflow.domain.model.TaskPriority.HIGH ->
                    com.rahul.fieldflow.features.tasks.model.TaskPriority.HIGH

                com.rahul.fieldflow.domain.model.TaskPriority.URGENT ->
                    com.rahul.fieldflow.features.tasks.model.TaskPriority.URGENT
            },
            assignedTo = assignedEmployee?.let {
                com.rahul.fieldflow.features.tasks.model.Employee(
                    it.id,
                    it.fullName,
                    "Employee",
                    it.avatarUrl,
                    it.employeeCode
                )
            } ?: com.rahul.fieldflow.features.tasks.model.Employee(
                "",
                "Unassigned",
                "Employee"
            ),
            location = location ?: "No location",
            latitude = latitude,
            longitude = longitude,
            radiusMeters = radiusMeters,
            scheduledDate = dueDate ?: createdAt
        )
    }
}