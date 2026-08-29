package com.rahul.fieldflow.data.notifications

import com.rahul.fieldflow.data.auth.AuthDataSource
import com.rahul.fieldflow.domain.model.AppNotification
import com.rahul.fieldflow.domain.repository.NotificationRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val dataSource: NotificationDataSource,
    private val authDataSource: AuthDataSource
) : NotificationRepository {

    override suspend fun getNotifications(): Result<List<AppNotification>> {
        return runCatching {
            val userId = authDataSource.getCurrentUserId() ?: throw Exception("Not logged in")
            dataSource.getNotifications(userId).map { it.toDomain() }
        }
    }

    override suspend fun markAsRead(notificationId: String): Result<Unit> {
        return runCatching {
            dataSource.markAsRead(notificationId)
        }
    }

    override suspend fun getUnreadCount(): Result<Int> {
        return runCatching {
            val userId = authDataSource.getCurrentUserId() ?: throw Exception("Not logged in")
            dataSource.getUnreadCount(userId)
        }
    }
}
