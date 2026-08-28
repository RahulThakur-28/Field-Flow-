package com.rahul.fieldflow.features.tasks.owner.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.fieldflow.domain.model.UserProfile
import com.rahul.fieldflow.domain.usecase.tasks.CreateTaskUseCase
import com.rahul.fieldflow.domain.usecase.workspace.GetWorkspaceEmployeesUseCase
import com.rahul.fieldflow.features.tasks.model.Employee
import com.rahul.fieldflow.features.tasks.model.SelectedLocation
import com.rahul.fieldflow.features.tasks.model.TaskPriority
import com.rahul.fieldflow.features.tasks.owner.state.CreateTaskUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class CreateTaskViewModel @Inject constructor(
    private val getWorkspaceEmployeesUseCase: GetWorkspaceEmployeesUseCase,
    private val createTaskUseCase: CreateTaskUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateTaskUiState())
    val uiState: StateFlow<CreateTaskUiState> = _uiState.asStateFlow()

    init {
        loadEmployees()
    }

    private fun loadEmployees() {
        viewModelScope.launch {
            Log.d("TASK_CREATE_DEBUG", "Loading employees for assignment...")
            _uiState.update { it.copy(isLoadingEmployees = true) }
            getWorkspaceEmployeesUseCase()
                .onSuccess { profiles ->
                    Log.d("TASK_CREATE_DEBUG", "Found ${profiles.size} workspace employees")
                    val employees = profiles.map { it.toEmployee() }
                    _uiState.update { it.copy(employees = employees, isLoadingEmployees = false) }
                }
                .onFailure { error ->
                    Log.e("TASK_CREATE_DEBUG", "Failed to load employees", error)
                    _uiState.update { it.copy(generalError = error.message, isLoadingEmployees = false) }
                }
        }
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
    fun updateRadius(radius: Int) = _uiState.update { it.copy(radiusMeters = radius.coerceIn(50, 100)) }

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

    fun createTask(onSuccess: () -> Unit) {
        if (!validateForm()) return

        val state = _uiState.value
        
        viewModelScope.launch {
            Log.d("TASK_LOCATION_DEBUG", "Before Create: location='${state.location}', lat=${state.latitude}, lng=${state.longitude}, radius=${state.radiusMeters}")
            _uiState.update { it.copy(isSaving = true, generalError = null) }
            
            // Map deadline to due_date for existing RPC
            val dueDateTime = state.date!!.atTime(state.deadline)
                .atZone(ZoneId.systemDefault())
                .toOffsetDateTime()
            
            createTaskUseCase(
                title = state.title.trim(),
                description = state.description.trim(),
                priority = when(state.priority) {
                    TaskPriority.LOW -> com.rahul.fieldflow.domain.model.TaskPriority.LOW
                    TaskPriority.MEDIUM -> com.rahul.fieldflow.domain.model.TaskPriority.MEDIUM
                    TaskPriority.HIGH -> com.rahul.fieldflow.domain.model.TaskPriority.HIGH
                    TaskPriority.URGENT -> com.rahul.fieldflow.domain.model.TaskPriority.URGENT
                },
                location = state.location.trim(),
                dueDate = dueDateTime,
                employeeId = state.selectedEmployee!!.id,
                latitude = state.latitude,
                longitude = state.longitude,
                radiusMeters = state.radiusMeters,
                checklistItems = state.checklist
            ).onSuccess {
                Log.d("TASK_LOCATION_DEBUG", "Task created successfully")
                _uiState.update { it.copy(isSaving = false) }
                onSuccess()
            }.onFailure { error ->
                Log.e("TASK_LOCATION_DEBUG", "Task creation failed", error)
                _uiState.update { it.copy(isSaving = false, generalError = error.message) }
            }
        }
    }

    private fun validateForm(): Boolean {
        val state = _uiState.value
        var isValid = true

        val titleError = if (state.title.trim().isBlank()) "Title is required" else null
        val descriptionError = if (state.description.trim().isBlank()) "Description is required" else null
        val employeeError = if (state.selectedEmployee == null) "Please select an employee" else null
        val locationError = if (state.location.trim().isBlank()) {
             "Destination name is required"
        } else if (state.latitude == null || state.longitude == null) {
            "Please select location on map"
        } else null
        
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

    private fun UserProfile.toEmployee(): Employee {
        return Employee(
            id = id,
            name = fullName,
            role = "Employee",
            avatarUrl = avatarUrl,
            employeeCode = employeeCode
        )
    }
}
