package com.rahul.fieldflow.features.tasks.owner.state

import com.rahul.fieldflow.features.tasks.model.Employee
import com.rahul.fieldflow.features.tasks.model.Task
import com.rahul.fieldflow.features.tasks.model.TaskPriority

data class OwnerTasksUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val selectedTab: Int = 0,
    val error: String? = null
)

data class CreateTaskUiState(
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val selectedEmployee: Employee? = null,
    val employees: List<Employee> = emptyList(),
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val date: String = "",
    val time: String = "",
    val isSaving: Boolean = false,
    val isLoadingEmployees: Boolean = false,
    val error: String? = null
)

data class OwnerTaskDetailsUiState(
    val task: Task? = null,
    val isLoading: Boolean = false
)

data class EditTaskUiState(
    val taskId: String = "",
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val selectedEmployee: Employee? = null,
    val employees: List<Employee> = emptyList(),
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val date: String = "",
    val time: String = "",
    val isSaving: Boolean = false,
    val isLoading: Boolean = false,
    val isLoadingEmployees: Boolean = false,
    val error: String? = null
)
