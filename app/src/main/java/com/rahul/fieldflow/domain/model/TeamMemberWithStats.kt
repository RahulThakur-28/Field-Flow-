package com.rahul.fieldflow.domain.model

data class TeamMemberWithStats(
    val employee: UserProfile,
    val totalTasks: Int,
    val completedTasks: Int,
    val currentTask: Task?
)
