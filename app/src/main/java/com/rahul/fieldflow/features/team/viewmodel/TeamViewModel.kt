package com.rahul.fieldflow.features.team.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.fieldflow.domain.model.TaskStatus
import com.rahul.fieldflow.domain.model.TeamMemberWithStats
import com.rahul.fieldflow.domain.usecase.team.GetEmployeeDetailsUseCase
import com.rahul.fieldflow.domain.usecase.team.GetTeamWithStatsUseCase
import com.rahul.fieldflow.features.team.model.EmployeeTeamUiModel
import com.rahul.fieldflow.features.team.state.EmployeeDetailsUiState
import com.rahul.fieldflow.features.team.state.TeamUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeamViewModel @Inject constructor(
    private val getTeamWithStatsUseCase: GetTeamWithStatsUseCase,
    private val getEmployeeDetailsUseCase: GetEmployeeDetailsUseCase
) : ViewModel() {
    private val _teamUiState = MutableStateFlow(TeamUiState())
    val teamUiState: StateFlow<TeamUiState> = _teamUiState.asStateFlow()

    private val _employeeDetailsUiState = MutableStateFlow(EmployeeDetailsUiState())
    val employeeDetailsUiState: StateFlow<EmployeeDetailsUiState> = _employeeDetailsUiState.asStateFlow()

    init {
        loadTeam()
    }

    fun loadTeam(isRefreshing: Boolean = false) {
        viewModelScope.launch {
            if (!isRefreshing) {
                _teamUiState.update { it.copy(isLoading = true) }
            } else {
                // We could add an isRefreshing state to UI state if needed, 
                // but usually isLoading is enough for standard indicators.
                _teamUiState.update { it.copy(isLoading = true) }
            }
            
            getTeamWithStatsUseCase()
                .onSuccess { members ->
                    val uiMembers = members.map { it.toUiModel() }
                    _teamUiState.update {
                        it.copy(
                            teamMembers = uiMembers,
                            filteredMembers = uiMembers,
                            isLoading = false,
                            teamMemberCount = uiMembers.size
                        )
                    }
                }
                .onFailure {
                    _teamUiState.update { it.copy(isLoading = false) }
                }
        }
    }

    fun refreshTeam() {
        loadTeam(isRefreshing = true)
    }

    fun onSearchQueryChange(query: String) {
        _teamUiState.update { state ->
            val filtered = if (query.isBlank()) {
                state.teamMembers
            } else {
                state.teamMembers.filter {
                    it.profile.fullName.contains(query, ignoreCase = true) ||
                            it.profile.email.contains(query, ignoreCase = true)
                }
            }
            state.copy(searchQuery = query, filteredMembers = filtered)
        }
    }

    private fun TeamMemberWithStats.toUiModel(): EmployeeTeamUiModel {
        return EmployeeTeamUiModel(
            profile = employee,
            totalTasks = totalTasks,
            completedTasks = completedTasks,
            currentTask = currentTask,
            status = if (currentTask != null) "Active" else "Idle"
        )
    }

    fun loadEmployeeDetails(employeeId: String, isRefreshing: Boolean = false) {
        viewModelScope.launch {
            if (!isRefreshing) {
                _employeeDetailsUiState.update { it.copy(isLoading = true) }
            }
            
            getEmployeeDetailsUseCase(employeeId)
                .onSuccess { details ->
                    _employeeDetailsUiState.update {
                        it.copy(
                            profile = details.profile,
                            currentTask = details.currentTask,
                            pastTasks = details.pastTasks,
                            totalTasks = details.pastTasks.size + (if (details.currentTask != null) 1 else 0),
                            completedTasks = details.pastTasks.size,
                            isLoading = false
                        )
                    }
                }
                .onFailure {
                    _employeeDetailsUiState.update { it.copy(isLoading = false) }
                }
        }
    }

    fun refreshEmployeeDetails(employeeId: String) {
        loadEmployeeDetails(employeeId, isRefreshing = true)
    }
}
