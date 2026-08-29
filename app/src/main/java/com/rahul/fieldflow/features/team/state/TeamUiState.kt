package com.rahul.fieldflow.features.team.state

import com.rahul.fieldflow.domain.model.Task
import com.rahul.fieldflow.domain.model.TeamMemberWithStats
import com.rahul.fieldflow.features.team.model.EmployeeTeamUiModel

data class TeamUiState(
    val teamMembers: List<EmployeeTeamUiModel> = emptyList(),
    val filteredMembers: List<EmployeeTeamUiModel> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val teamMemberCount: Int = 0
)

data class EmployeeDetailsUiState(
    val profile: com.rahul.fieldflow.domain.model.UserProfile? = null,
    val currentTask: Task? = null,
    val pastTasks: List<Task> = emptyList(),
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val isLoading: Boolean = false
)
