package com.rahul.fieldflow.features.home.owner.state

import com.rahul.fieldflow.features.home.model.ActivityItemUiModel
import com.rahul.fieldflow.features.home.model.FieldVisitUiModel
import com.rahul.fieldflow.features.home.model.SummaryStatUiModel
import com.rahul.fieldflow.features.home.model.TeamMemberUiModel

data class OwnerHomeUiState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val location: String = "",
    val initials: String = "",
    val notificationCount: Int = 0,
    val stats: List<SummaryStatUiModel> = emptyList(),
    val liveVisits: List<FieldVisitUiModel> = emptyList(),
    val teamStatus: List<TeamMemberUiModel> = emptyList(),
    val recentActivity: List<ActivityItemUiModel> = emptyList()
)
