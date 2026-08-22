package com.rahul.fieldflow.features.home.employee.state

import com.rahul.fieldflow.features.home.model.NextTaskUiModel
import com.rahul.fieldflow.features.home.model.QuickAccessUiModel
import com.rahul.fieldflow.features.home.model.ScheduleTaskUiModel
import com.rahul.fieldflow.features.home.model.SummaryStatUiModel

data class EmployeeHomeUiState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val date: String = "",
    val initials: String = "",
    val notificationCount: Int = 0,
    val stats: List<SummaryStatUiModel> = emptyList(),
    val nextTask: NextTaskUiModel? = null,
    val schedule: List<ScheduleTaskUiModel> = emptyList(),
    val quickAccess: List<QuickAccessUiModel> = emptyList()
)
