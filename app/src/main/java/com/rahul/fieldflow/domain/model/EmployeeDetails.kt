package com.rahul.fieldflow.domain.model

data class EmployeeDetails(
    val profile: UserProfile,
    val currentTask: Task?,
    val pastTasks: List<Task>
)
