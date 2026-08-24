package com.rahul.fieldflow.features.tasks.owner.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.fieldflow.domain.model.UserProfile
import com.rahul.fieldflow.domain.usecase.tasks.CreateTaskUseCase
import com.rahul.fieldflow.domain.usecase.workspace.GetWorkspaceEmployeesUseCase
import com.rahul.fieldflow.features.tasks.model.Employee
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
import java.time.format.DateTimeFormatter
import java.util.Locale
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
                    profiles.forEach { 
                        Log.d("TASK_CREATE_DEBUG", "Employee: ${it.fullName}, ID: ${it.id}, Role: ${it.role}")
                    }
                    val employees = profiles.map { it.toEmployee() }
                    _uiState.update { it.copy(employees = employees, isLoadingEmployees = false) }
                }
                .onFailure { error ->
                    Log.e("TASK_CREATE_DEBUG", "Failed to load employees", error)
                    _uiState.update { it.copy(error = error.message, isLoadingEmployees = false) }
                }
        }
    }

    fun updateTitle(title: String) = _uiState.update { it.copy(title = title) }
    fun updateDescription(desc: String) = _uiState.update { it.copy(description = desc) }
    fun updateLocation(loc: String) = _uiState.update { it.copy(location = loc) }
    fun updateEmployee(emp: Employee) = _uiState.update { it.copy(selectedEmployee = emp) }
    fun updatePriority(prio: TaskPriority) = _uiState.update { it.copy(priority = prio) }
    fun updateDate(date: String) = _uiState.update { it.copy(date = date) }
    fun updateTime(time: String) = _uiState.update { it.copy(time = time) }

    fun createTask(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.title.isBlank()) {
            _uiState.update { it.copy(error = "Title is required") }
            return
        }
        if (state.selectedEmployee == null) {
            _uiState.update { it.copy(error = "Please select an employee") }
            return
        }

        val dueDate = parseDateTime(state.date, state.time)
        if (dueDate == null && (state.date.isNotBlank() || state.time.isNotBlank())) {
            _uiState.update { it.copy(error = "Invalid date or time format. Please use DD/MM/YYYY and HH:mm") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            
            createTaskUseCase(
                title = state.title,
                description = state.description,
                priority = when(state.priority) {
                    TaskPriority.LOW -> com.rahul.fieldflow.domain.model.TaskPriority.LOW
                    TaskPriority.MEDIUM -> com.rahul.fieldflow.domain.model.TaskPriority.MEDIUM
                    TaskPriority.HIGH -> com.rahul.fieldflow.domain.model.TaskPriority.HIGH
                    TaskPriority.URGENT -> com.rahul.fieldflow.domain.model.TaskPriority.URGENT
                },
                location = state.location,
                dueDate = dueDate,
                employeeId = state.selectedEmployee.id
            ).onSuccess {
                _uiState.update { it.copy(isSaving = false) }
                onSuccess()
            }.onFailure { error ->
                _uiState.update { it.copy(isSaving = false, error = error.message) }
            }
        }
    }

    private fun parseDateTime(date: String, time: String): OffsetDateTime? {
        val trimmedDate = date.trim()
        val trimmedTime = time.trim()

        if (trimmedDate.isBlank() || trimmedTime.isBlank()) {
            return null
        }

        return try {
            Log.d(
                "TASK_CREATE_DEBUG",
                "Parsing date: '$trimmedDate', time: '$trimmedTime'"
            )

            /*
             * Supported date formats:
             * 12/26/2004 -> MM/dd/yyyy
             * 26/12/2004 -> dd/MM/yyyy
             * 2026-08-24 -> yyyy-MM-dd
             */
            val dateFormatters = listOf(
                DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.US),
                DateTimeFormatter.ofPattern("M/d/yyyy", Locale.US),
                DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.US),
                DateTimeFormatter.ofPattern("d/M/yyyy", Locale.US),
                DateTimeFormatter.ISO_LOCAL_DATE
            )

            var datePart: LocalDate? = null

            for (formatter in dateFormatters) {
                try {
                    datePart = LocalDate.parse(trimmedDate, formatter)
                    break
                } catch (_: Exception) {
                    // Try next format
                }
            }

            if (datePart == null) {
                throw Exception("Could not parse date: $trimmedDate")
            }

            // Prevent obviously corrupted dates
            if (datePart.year < 2020 || datePart.year > 2100) {
                throw Exception(
                    "Invalid year: ${datePart.year}"
                )
            }

            /*
             * Supported time formats:
             * 12:34
             * 3:00
             * 03:00 PM
             * 3:00 PM
             */
            val timeFormatters = listOf(
                DateTimeFormatter.ofPattern("HH:mm", Locale.US),
                DateTimeFormatter.ofPattern("H:mm", Locale.US),
                DateTimeFormatter.ofPattern("hh:mm a", Locale.US),
                DateTimeFormatter.ofPattern("h:mm a", Locale.US)
            )

            var timePart: LocalTime? = null

            for (formatter in timeFormatters) {
                try {
                    timePart = LocalTime.parse(
                        trimmedTime.uppercase(Locale.US),
                        formatter
                    )
                    break
                } catch (_: Exception) {
                    // Try next format
                }
            }

            if (timePart == null) {
                throw Exception("Could not parse time: $trimmedTime")
            }

            val localDateTime = java.time.LocalDateTime.of(
                datePart,
                timePart
            )

            val result = localDateTime
                .atZone(ZoneId.systemDefault())
                .toOffsetDateTime()

            Log.d(
                "TASK_CREATE_DEBUG",
                "Parsed successfully: $result"
            )

            result

        } catch (e: Exception) {
            Log.e(
                "CreateTaskViewModel",
                "Failed to parse date/time: $trimmedDate $trimmedTime",
                e
            )

            null
        }
    }

    private fun UserProfile.toEmployee(): Employee {
        return Employee(
            id = id,
            name = fullName,
            role = "Employee",
            avatarUrl = avatarUrl
        )
    }
}
