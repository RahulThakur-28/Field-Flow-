package com.rahul.fieldflow.features.tasks.owner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.fieldflow.features.tasks.model.Employee
import com.rahul.fieldflow.features.tasks.model.TaskPriority
import com.rahul.fieldflow.features.tasks.model.mockTasks
import com.rahul.fieldflow.features.tasks.owner.state.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

class OwnerTasksViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OwnerTasksUiState())
    val uiState: StateFlow<OwnerTasksUiState> = _uiState.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(1000) // Simulate network
            _uiState.update { it.copy(tasks = mockTasks, isLoading = false) }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }
}

class CreateTaskViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CreateTaskUiState())
    val uiState: StateFlow<CreateTaskUiState> = _uiState.asStateFlow()

    fun updateTitle(title: String) = _uiState.update { it.copy(title = title) }
    fun updateDescription(desc: String) = _uiState.update { it.copy(description = desc) }
    fun updateLocation(loc: String) = _uiState.update { it.copy(location = loc) }
    fun updateEmployee(emp: Employee) = _uiState.update { it.copy(selectedEmployee = emp) }
    fun updatePriority(prio: TaskPriority) = _uiState.update { it.copy(priority = prio) }
    fun updateDate(date: String) = _uiState.update { it.copy(date = date) }
    fun updateTime(time: String) = _uiState.update { it.copy(time = time) }

    fun createTask(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            delay(1500)
            _uiState.update { it.copy(isSaving = false) }
            onSuccess()
        }
    }
}

class OwnerTaskDetailsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OwnerTaskDetailsUiState())
    val uiState: StateFlow<OwnerTaskDetailsUiState> = _uiState.asStateFlow()

    fun loadTask(taskId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(800)
            val task = mockTasks.find { it.id == taskId }
            _uiState.update { it.copy(task = task, isLoading = false) }
        }
    }
}

class EditTaskViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(EditTaskUiState())
    val uiState: StateFlow<EditTaskUiState> = _uiState.asStateFlow()

    fun loadTask(taskId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(800)
            val task = mockTasks.find { it.id == taskId }
            task?.let { t ->
                _uiState.update {
                    it.copy(
                        taskId = t.id,
                        title = t.title,
                        description = t.description,
                        location = t.location,
                        selectedEmployee = t.assignedTo,
                        priority = t.priority,
                        date = t.scheduledDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                        time = t.scheduledDate.format(DateTimeFormatter.ofPattern("HH:mm")),
                        isLoading = false
                    )
                }
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

    fun saveTask(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            delay(1500)
            _uiState.update { it.copy(isSaving = false) }
            onSuccess()
        }
    }
}
