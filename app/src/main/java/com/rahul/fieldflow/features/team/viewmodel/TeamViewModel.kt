package com.rahul.fieldflow.features.team.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.fieldflow.features.tasks.model.mockTasks
import com.rahul.fieldflow.features.team.model.EmployeeActivity
import com.rahul.fieldflow.features.team.model.EmployeeTeamUiModel
import com.rahul.fieldflow.features.team.model.mockEmployeeActivities
import com.rahul.fieldflow.features.team.model.mockTeamMembers
import com.rahul.fieldflow.features.team.state.EmployeeDetailsUiState
import com.rahul.fieldflow.features.team.state.TeamUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TeamViewModel : ViewModel() {
    private val _teamUiState = MutableStateFlow(TeamUiState())
    val teamUiState: StateFlow<TeamUiState> = _teamUiState.asStateFlow()

    private val _employeeDetailsUiState = MutableStateFlow(EmployeeDetailsUiState())
    val employeeDetailsUiState: StateFlow<EmployeeDetailsUiState> = _employeeDetailsUiState.asStateFlow()

    init {
        loadTeam()
    }

    private fun loadTeam() {
        viewModelScope.launch {
            _teamUiState.update { it.copy(isLoading = true) }
            delay(1000) // Simulate network
            _teamUiState.update {
                it.copy(
                    teamMembers = mockTeamMembers,
                    filteredMembers = mockTeamMembers,
                    isLoading = false,
                    activeCount = 3,
                    avgOnTime = 91,
                    totalTasks = 100
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _teamUiState.update { state ->
            val filtered = if (query.isBlank()) {
                state.teamMembers
            } else {
                state.teamMembers.filter {
                    it.employee.name.contains(query, ignoreCase = true) ||
                            it.employee.role.contains(query, ignoreCase = true)
                }
            }
            state.copy(searchQuery = query, filteredMembers = filtered)
        }
    }

    fun loadEmployeeDetails(employeeId: String) {
        viewModelScope.launch {
            _employeeDetailsUiState.update { it.copy(isLoading = true) }
            delay(800)
            val member = mockTeamMembers.find { it.employee.id == employeeId }
            val currentTask = mockTasks.find { it.assignedTo.id == employeeId && it.id == "1" } // Mock current task logic
            val assignedTasks = mockTasks.filter { it.assignedTo.id == employeeId }
            
            _employeeDetailsUiState.update {
                it.copy(
                    member = member,
                    currentTask = currentTask,
                    recentActivity = mockEmployeeActivities,
                    assignedTasks = assignedTasks,
                    isLoading = false
                )
            }
        }
    }
}
