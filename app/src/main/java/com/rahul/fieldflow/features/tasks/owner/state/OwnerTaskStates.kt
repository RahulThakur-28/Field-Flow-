package com.rahul.fieldflow.features.tasks.owner.state

import com.rahul.fieldflow.features.tasks.model.Employee
import com.rahul.fieldflow.features.tasks.model.Task
import com.rahul.fieldflow.features.tasks.model.TaskPriority
import java.time.LocalDate
import java.time.LocalTime

data class OwnerTasksUiState(
    val tasks: List<Task> = emptyList(),
    val filteredTasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val selectedFilter: TaskFilter = TaskFilter.ALL,
    val error: String? = null
)

enum class TaskFilter(val label: String) {
    ALL("All"),
    ACTIVE("Active"),
    COMPLETED("Completed"),
    OVERDUE("Overdue")
}

data class CreateTaskUiState(
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val radiusMeters: Int = 50,
    val selectedEmployee: Employee? = null,
    val employees: List<Employee> = emptyList(),
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val date: LocalDate? = null,
    val startTime: LocalTime? = null,
    val deadline: LocalTime? = null,
    val instructions: String = "",
    val checklist: List<String> = emptyList(),
    
    val isSaving: Boolean = false,
    val isLoadingEmployees: Boolean = false,
    
    // Validation Errors
    val titleError: String? = null,
    val descriptionError: String? = null,
    val employeeError: String? = null,
    val locationError: String? = null,
    val dateError: String? = null,
    val startTimeError: String? = null,
    val deadlineError: String? = null,
    val generalError: String? = null
)

data class OwnerTaskDetailsUiState(
    val task: Task? = null,
    val isLoading: Boolean = false
)

data class EditTaskUiState(
    val taskId: String = "",
    val title: String = "",
    val titleError: String? = null,
    val description: String = "",
    val descriptionError: String? = null,
    val location: String = "",
    val locationError: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val radiusMeters: Int = 50,
    val selectedEmployee: Employee? = null,
    val employees: List<Employee> = emptyList(),
    val employeeError: String? = null,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val date: LocalDate? = null,
    val dateError: String? = null,
    val startTime: LocalTime? = null,
    val startTimeError: String? = null,
    val deadline: LocalTime? = null,
    val deadlineError: String? = null,
    val instructions: String = "",
    val checklist: List<String> = emptyList(),
    
    val isSaving: Boolean = false,
    val isLoading: Boolean = false,
    val isLoadingEmployees: Boolean = false,
    val error: String? = null
)
