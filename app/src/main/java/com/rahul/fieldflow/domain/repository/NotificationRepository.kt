package com.rahul.fieldflow.domain.repository

import com.rahul.fieldflow.domain.model.AppNotification

interface NotificationRepository {
    suspend fun getNotifications(): Result<List<AppNotification>>
    suspend fun markAsRead(notificationId: String): Result<Unit>
    suspend fun getUnreadCount(): Result<Int>
}
