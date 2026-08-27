package com.rahul.fieldflow.features.tasks.owner.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.fieldflow.domain.model.UserProfile
import com.rahul.fieldflow.domain.repository.TaskRepository
import com.rahul.fieldflow.domain.usecase.workspace.GetWorkspaceEmployeesUseCase
import com.rahul.fieldflow.features.tasks.model.Employee
import com.rahul.fieldflow.features.tasks.model.SelectedLocation
import com.rahul.fieldflow.features.tasks.model.TaskPriority
import com.rahul.fieldflow.features.tasks.owner.state.EditTaskUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class EditTaskViewModel @Inject constructor(
    private val getWorkspaceEmployeesUseCase: GetWorkspaceEmployeesUseCase,
    private val taskRepository: TaskRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(EditTaskUiState())
    val uiState: StateFlow<EditTaskUiState> = _uiState.asStateFlow()

    init {
        loadEmployees()
    }

    private fun loadEmployees() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingEmployees = true) }
            getWorkspaceEmployeesUseCase()
                .onSuccess { profiles ->
                    val employees = profiles.map { it.toEmployee() }
                    _uiState.update { it.copy(employees = employees, isLoadingEmployees = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message, isLoadingEmployees = false) }
                }
        }
    }

    fun loadTask(taskId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            taskRepository.getTaskById(taskId).onSuccess { t ->
                _uiState.update {
                    it.copy(
                        taskId = t.id,
                        title = t.title,
                        description = t.description ?: "",
                        location = t.location ?: "",
                        latitude = t.latitude,
                        longitude = t.longitude,
                        radiusMeters = t.radiusMeters,
                        selectedEmployee = t.assignedEmployee?.toEmployee(),
                        priority = when(t.priority) {
                            com.rahul.fieldflow.domain.model.TaskPriority.LOW -> TaskPriority.LOW
                            com.rahul.fieldflow.domain.model.TaskPriority.MEDIUM -> TaskPriority.MEDIUM
                            com.rahul.fieldflow.domain.model.TaskPriority.HIGH -> TaskPriority.HIGH
                            com.rahul.fieldflow.domain.model.TaskPriority.URGENT -> TaskPriority.URGENT
                        },
                        date = t.dueDate?.toLocalDate(),
                        startTime = t.dueDate?.toLocalTime(), 
                        deadline = t.dueDate?.toLocalTime(),
                        isLoading = false
                    )
                }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message) }
            }
        }
    }

    private fun UserProfile.toEmployee(): Employee {
        return Employee(
            id = id,
            name = fullName,
            role = "Employee",
            avatarUrl = avatarUrl,
            employeeCode = employeeCode
        )
    }

    fun updateTitle(title: String) = _uiState.update { it.copy(title = title, titleError = null) }
    fun updateDescription(desc: String) = _uiState.update { it.copy(description = desc, descriptionError = null) }
    fun updateLocation(loc: String) = _uiState.update { it.copy(location = loc, locationError = null) }
    fun updateEmployee(emp: Employee) = _uiState.update { it.copy(selectedEmployee = emp, employeeError = null) }
    fun updatePriority(prio: TaskPriority) = _uiState.update { it.copy(priority = prio) }
    fun updateDate(date: LocalDate) = _uiState.update { it.copy(date = date, dateError = null) }
    fun updateStartTime(time: LocalTime) = _uiState.update { it.copy(startTime = time, startTimeError = null) }
    fun updateDeadline(time: LocalTime) = _uiState.update { it.copy(deadline = time, deadlineError = null) }
    fun updateInstructions(instructions: String) = _uiState.update { it.copy(instructions = instructions) }

    fun onLocationSelected(selectedLocation: SelectedLocation) {
        _uiState.update {
            it.copy(
                location = if (it.location.isNotBlank()) it.location else (selectedLocation.address ?: "Selected map location"),
                latitude = selectedLocation.latitude,
                longitude = selectedLocation.longitude,
                radiusMeters = selectedLocation.radiusMeters.coerceIn(50, 100),
                locationError = null
            )
        }
    }

    fun addChecklistItem(item: String) {
        if (item.isNotBlank() && !_uiState.value.checklist.contains(item.trim())) {
            _uiState.update { it.copy(checklist = it.checklist + item.trim()) }
        }
    }

    fun removeChecklistItem(item: String) {
        _uiState.update { it.copy(checklist = it.checklist - item) }
    }

    fun saveTask(onSuccess: () -> Unit) {
        if (!validateForm()) return

        val state = _uiState.value
        Log.d("TASK_LOCATION_DEBUG", "Before Save (Edit): location='${state.location}', lat=${state.latitude}, lng=${state.longitude}, radius=${state.radiusMeters}")

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            // Simulating update logic for now
            delay(1500)
            _uiState.update { it.copy(isSaving = false) }
            onSuccess()
        }
    }

    private fun validateForm(): Boolean {
        val state = _uiState.value
        var isValid = true

        val titleError = if (state.title.trim().isBlank()) "Title is required" else null
        val descriptionError = if (state.description.trim().isBlank()) "Description is required" else null
        val employeeError = if (state.selectedEmployee == null) "Please select an employee" else null
        val locationError = if (state.location.trim().isBlank()) "Destination is required" else null
        val dateError = if (state.date == null) "Date is required" else null
        val startTimeError = if (state.startTime == null) "Start time is required" else null
        val deadlineError = if (state.deadline == null) {
            "Deadline is required"
        } else if (state.startTime != null && state.deadline.isBefore(state.startTime)) {
            "Deadline must be after start time"
        } else null

        if (titleError != null || descriptionError != null || employeeError != null || 
            locationError != null || dateError != null || startTimeError != null || deadlineError != null) {
            isValid = false
        }

        _uiState.update {
            it.copy(
                titleError = titleError,
                descriptionError = descriptionError,
                employeeError = employeeError,
                locationError = locationError,
                dateError = dateError,
                startTimeError = startTimeError,
                deadlineError = deadlineError
            )
        }

        return isValid
    }
}
