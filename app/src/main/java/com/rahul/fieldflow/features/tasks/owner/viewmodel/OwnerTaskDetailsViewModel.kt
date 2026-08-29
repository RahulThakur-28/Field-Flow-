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
import java.time.OffsetDateTime
import javax.inject.Inject

@HiltViewModel
class OwnerTaskDetailsViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OwnerTaskDetailsUiState())
    val uiState: StateFlow<OwnerTaskDetailsUiState> = _uiState.asStateFlow()

    fun loadTask(taskId: String) {
        viewModelScope.launch {
            Log.d("OWNER_CHECKLIST_TRACE", "loadTask: taskId=$taskId")

            _uiState.update {
                it.copy(
                    isLoading = true
                )
            }

            taskRepository.getTaskById(taskId)
                .onSuccess { task ->
                    Log.d("OWNER_CHECKLIST_TRACE", "loadTask: Domain Task returned. checklist size=${task.checklist.size}")

                    val uiTask = task.toUiTask()
                    Log.d("OWNER_CHECKLIST_TRACE", "loadTask: mapped to UI Task. checklist count=${uiTask.checklist.size}")
                    
                    val completedCount = uiTask.checklist.count { it.isChecked }
                    val totalCount = uiTask.checklist.size
                    Log.d("OWNER_CHECKLIST_TRACE", "loadTask: completedCount=$completedCount, totalCount=$totalCount")

                    _uiState.update {
                        it.copy(
                            task = uiTask,
                            isLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    Log.e("OWNER_CHECKLIST_TRACE", "loadTask: failure class=${error.javaClass.simpleName}, message=${error.message}")

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

        val uiStatus = if (status != com.rahul.fieldflow.domain.model.TaskStatus.COMPLETED && 
            dueDate?.isBefore(OffsetDateTime.now()) == true) {
            com.rahul.fieldflow.features.tasks.model.TaskStatus.OVERDUE
        } else {
            when (status) {
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
            }
        }

        return com.rahul.fieldflow.features.tasks.model.Task(
            id = id,
            title = title,
            description = description ?: "",
            status = uiStatus,
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
            scheduledDate = dueDate ?: createdAt,
            deadline = dueDate,
            checklist = checklist.map { 
                com.rahul.fieldflow.features.tasks.model.ChecklistItem(
                    id = it.id,
                    title = it.itemText,
                    isChecked = it.isCompleted
                )
            }
        )
    }
}
