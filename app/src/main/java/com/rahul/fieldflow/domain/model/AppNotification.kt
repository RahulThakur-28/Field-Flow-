package com.rahul.fieldflow.domain.model

import java.time.OffsetDateTime

data class AppNotification(
    val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: String,
    val isRead: Boolean,
    val createdAt: OffsetDateTime
)
