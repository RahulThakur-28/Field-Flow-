package com.rahul.fieldflow.features.home.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Description
import com.rahul.fieldflow.features.home.employee.state.EmployeeHomeUiState

fun dummyEmployeeHomeUiState() = EmployeeHomeUiState(
    userName = "Rahul",
    initials = "RT",
    allTasksCount = 12,
    activeTasksCount = 3,
    completedTasksCount = 7
)
