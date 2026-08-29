package com.rahul.fieldflow.data.notifications

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject

class NotificationDataSource @Inject constructor(
    private val supabaseClient: SupabaseClient
) {
    suspend fun getNotifications(userId: String): List<NotificationDto> {
        return supabaseClient.postgrest["notifications"]
            .select {
                filter {
                    eq("user_id", userId)
                }
                order("created_at", order = Order.DESCENDING)
            }
            .decodeList<NotificationDto>()
    }

    suspend fun markAsRead(notificationId: String) {
        supabaseClient.postgrest["notifications"].update(
            buildJsonObject {
                put("is_read", true)
            }
        ) {
            filter {
                eq("id", notificationId)
            }
        }
    }

    suspend fun getUnreadCount(userId: String): Int {
        val response = supabaseClient.postgrest["notifications"]
            .select {
                filter {
                    eq("user_id", userId)
                    eq("is_read", false)
                }
            }
        return response.decodeList<NotificationDto>().size
    }
}
