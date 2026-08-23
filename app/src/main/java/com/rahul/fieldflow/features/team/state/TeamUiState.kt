package com.rahul.fieldflow.features.team.state

import com.rahul.fieldflow.features.team.model.EmployeeActivity
import com.rahul.fieldflow.features.team.model.EmployeeTeamUiModel
import com.rahul.fieldflow.features.tasks.model.Task

data class TeamUiState(
    val teamMembers: List<EmployeeTeamUiModel> = emptyList(),
    val filteredMembers: List<EmployeeTeamUiModel> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val activeCount: Int = 0,
    val avgOnTime: Int = 0,
    val totalTasks: Int = 0
)

data class EmployeeDetailsUiState(
    val member: EmployeeTeamUiModel? = null,
    val currentTask: Task? = null,
    val recentActivity: List<EmployeeActivity> = emptyList(),
    val assignedTasks: List<Task> = emptyList(),
    val isLoading: Boolean = false
)
