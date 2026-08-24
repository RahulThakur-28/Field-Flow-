package com.rahul.fieldflow.features.team.state

import com.rahul.fieldflow.domain.model.JoinRequest

data class EmployeeRequestsUiState(
    val requests: List<JoinRequest> = emptyList(),
    val isLoading: Boolean = false,
    val processingRequestId: String? = null,
    val error: String? = null
)
