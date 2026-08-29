package com.rahul.fieldflow.features.notifications.state

import com.rahul.fieldflow.domain.model.AppNotification

data class NotificationUiState(
    val notifications: List<AppNotification> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
