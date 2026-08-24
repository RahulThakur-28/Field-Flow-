package com.rahul.fieldflow.features.tasks.owner.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.fieldflow.domain.model.UserProfile
import com.rahul.fieldflow.domain.repository.TaskRepository
import com.rahul.fieldflow.domain.usecase.workspace.GetWorkspaceEmployeesUseCase
import com.rahul.fieldflow.features.tasks.model.Employee
import com.rahul.fieldflow.features.tasks.model.TaskPriority
import com.rahul.fieldflow.features.tasks.model.mockTasks
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
            // Fetch real task from repository
            taskRepository.getOwnerTasks().onSuccess { tasks ->
                val task = tasks.find { it.id == taskId }
                task?.let { t ->
                    _uiState.update {
                        it.copy(
                            taskId = t.id,
                            title = t.title,
                            description = t.description ?: "",
                            location = t.location ?: "",
                            selectedEmployee = t.assignedEmployee?.toEmployee(),
                            priority = when(t.priority) {
                                com.rahul.fieldflow.domain.model.TaskPriority.LOW -> TaskPriority.LOW
                                com.rahul.fieldflow.domain.model.TaskPriority.MEDIUM -> TaskPriority.MEDIUM
                                com.rahul.fieldflow.domain.model.TaskPriority.HIGH -> TaskPriority.HIGH
                                com.rahul.fieldflow.domain.model.TaskPriority.URGENT -> TaskPriority.URGENT
                            },
                            date = t.dueDate?.format(DateTimeFormatter.ISO_LOCAL_DATE) ?: "",
                            time = t.dueDate?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "",
                            isLoading = false
                        )
                    }
                } ?: run {
                    _uiState.update { it.copy(isLoading = false, error = "Task not found") }
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

    fun updateTitle(title: String) = _uiState.update { it.copy(title = title) }
    fun updateDescription(desc: String) = _uiState.update { it.copy(description = desc) }
    fun updateLocation(loc: String) = _uiState.update { it.copy(location = loc) }
    fun updateEmployee(emp: Employee) = _uiState.update { it.copy(selectedEmployee = emp) }
    fun updatePriority(prio: TaskPriority) = _uiState.update { it.copy(priority = prio) }
    fun updateDate(date: String) = _uiState.update { it.copy(date = date) }
    fun updateTime(time: String) = _uiState.update { it.copy(time = time) }

    fun saveTask(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.title.isBlank()) {
            _uiState.update { it.copy(error = "Title is required") }
            return
        }
        
        val dueDate = parseDateTime(state.date, state.time)
        if (dueDate == null && (state.date.isNotBlank() || state.time.isNotBlank())) {
            _uiState.update { it.copy(error = "Invalid date or time format. Please use DD/MM/YYYY and HH:mm") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            // For now just simulate, but we should eventually call a repository method
            delay(1500)
            _uiState.update { it.copy(isSaving = false) }
            onSuccess()
        }
    }

    private fun parseDateTime(date: String, time: String): OffsetDateTime? {
        val trimmedDate = date.trim()
        val trimmedTime = time.trim()
        if (trimmedDate.isBlank() || trimmedTime.isBlank()) return null
        
        return try {
            Log.d("TASK_EDIT_DEBUG", "Parsing date: '$trimmedDate', time: '$trimmedTime'")
            
            val dateSelectors = listOf(
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("d/M/yyyy"),
                DateTimeFormatter.ISO_LOCAL_DATE
            )
            
            var datePart: LocalDate? = null
            for (formatter in dateSelectors) {
                try {
                    datePart = LocalDate.parse(trimmedDate, formatter)
                    break
                } catch (e: Exception) {}
            }
            
            if (datePart == null) throw Exception("Could not parse date")
            
            if (datePart.year > 2100 || datePart.year < 2020) {
                throw Exception("Invalid year")
            }

            val timeSelectors = listOf(
                DateTimeFormatter.ofPattern("HH:mm"),
                DateTimeFormatter.ofPattern("H:mm"),
                DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH),
                DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH)
            )

            var timePart: LocalTime? = null
            for (formatter in timeSelectors) {
                try {
                    timePart = LocalTime.parse(trimmedTime.uppercase(), formatter)
                    break
                } catch (e: Exception) {}
            }

            if (timePart == null) throw Exception("Could not parse time")

            val localDateTime = java.time.LocalDateTime.of(datePart, timePart)
            val result = localDateTime.atZone(ZoneId.systemDefault()).toOffsetDateTime()
            
            Log.d("TASK_EDIT_DEBUG", "Parsed successfully: $result")
            result
        } catch (e: Exception) {
            Log.e("EditTaskViewModel", "Failed to parse date/time: $trimmedDate $trimmedTime", e)
            null
        }
    }
}
