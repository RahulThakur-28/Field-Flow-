package com.rahul.fieldflow.features.team.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.fieldflow.domain.model.UserProfile
import com.rahul.fieldflow.domain.repository.AuthRepository
import com.rahul.fieldflow.features.tasks.model.Employee
import com.rahul.fieldflow.features.team.model.EmployeePerformance
import com.rahul.fieldflow.features.team.model.EmployeeTeamUiModel
import com.rahul.fieldflow.features.team.state.EmployeeDetailsUiState
import com.rahul.fieldflow.features.team.state.TeamUiState
import com.rahul.fieldflow.features.tasks.model.mockTasks
import com.rahul.fieldflow.features.team.model.mockEmployeeActivities
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeamViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _teamUiState = MutableStateFlow(TeamUiState())
    val teamUiState: StateFlow<TeamUiState> = _teamUiState.asStateFlow()

    private val _employeeDetailsUiState = MutableStateFlow(EmployeeDetailsUiState())
    val employeeDetailsUiState: StateFlow<EmployeeDetailsUiState> = _employeeDetailsUiState.asStateFlow()

    init {
        loadTeam()
    }

    fun loadTeam() {
        viewModelScope.launch {
            _teamUiState.update { it.copy(isLoading = true) }
            
            val user = authRepository.currentUser.first()
            val workspaceId = user?.workspaceId
            
            if (workspaceId != null) {
                authRepository.getTeamMembers(workspaceId)
                    .onSuccess { members ->
                        val uiMembers = members.map { it.toUiModel() }
                        _teamUiState.update {
                            it.copy(
                                teamMembers = uiMembers,
                                filteredMembers = uiMembers,
                                isLoading = false,
                                activeCount = uiMembers.count { m -> m.status == "Active" },
                                avgOnTime = 0, // Placeholder for now
                                totalTasks = 0 // Placeholder for now
                            )
                        }
                    }
                    .onFailure {
                        _teamUiState.update { it.copy(isLoading = false) }
                    }
            } else {
                _teamUiState.update { it.copy(isLoading = false) }
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

    private fun UserProfile.toUiModel(): EmployeeTeamUiModel {
        return EmployeeTeamUiModel(
            employee = Employee(
                id = id,
                name = fullName,
                role = "Employee", // Default or fetch if exists
                avatarUrl = avatarUrl
            ),
            currentTaskTitle = null, // Placeholder
            status = "Idle", // Placeholder
            performance = EmployeePerformance(0, 0, 0)
        )
    }

    fun loadEmployeeDetails(employeeId: String) {
        viewModelScope.launch {
            _employeeDetailsUiState.update { it.copy(isLoading = true) }
            // Fetch real details if needed, for now mock to keep it working
            val member = _teamUiState.value.teamMembers.find { it.employee.id == employeeId }
            _employeeDetailsUiState.update {
                it.copy(
                    member = member,
                    isLoading = false,
                    recentActivity = mockEmployeeActivities,
                    assignedTasks = mockTasks.filter { t -> t.assignedTo.id == employeeId }
                )
            }
        }
    }
}
