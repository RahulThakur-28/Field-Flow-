package com.rahul.fieldflow.data.notifications

import com.rahul.fieldflow.domain.model.AppNotification
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class NotificationDto(
    @SerialName("id") val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("title") val title: String,
    @SerialName("message") val message: String,
    @SerialName("type") val type: String,
    @SerialName("is_read") val isRead: Boolean,
    @SerialName("created_at") val createdAt: String
) {
    fun toDomain(): AppNotification {
        return AppNotification(
            id = id,
            userId = userId,
            title = title,
            message = message,
            type = type,
            isRead = isRead,
            createdAt = OffsetDateTime.parse(createdAt, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        )
    }
}
