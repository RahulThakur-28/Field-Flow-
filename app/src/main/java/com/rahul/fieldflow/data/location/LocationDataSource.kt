package com.rahul.fieldflow.data.location

import com.rahul.fieldflow.domain.model.LocationPoint
import com.rahul.fieldflow.domain.model.LocationSession
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class LocationDataSource @Inject constructor(
    private val supabaseClient: SupabaseClient
) {
    suspend fun createSession(taskId: String, employeeId: String): LocationSessionDto {
        return supabaseClient.postgrest["location_sessions"].insert(
            buildJsonObject {
                put("task_id", taskId)
                put("employee_id", employeeId)
                put("started_at", OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                put("status", "active")
            }
        ) {
            select()
        }.decodeSingle<LocationSessionDto>()
    }

    suspend fun getActiveSession(taskId: String, employeeId: String): LocationSessionDto? {
        return supabaseClient.postgrest["location_sessions"]
            .select {
                filter {
                    eq("task_id", taskId)
                    eq("employee_id", employeeId)
                    filter("ended_at", FilterOperator.IS, "null") 
                }
            }
            .decodeSingleOrNull<LocationSessionDto>()
    }

    suspend fun updateSessionEnd(sessionId: String) {
        supabaseClient.postgrest["location_sessions"].update(
            buildJsonObject {
                put("ended_at", OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                put("status", "completed")
            }
        ) {
            filter {
                eq("id", sessionId)
            }
        }
    }

    suspend fun insertPoint(point: LocationPoint) {
        supabaseClient.postgrest["location_points"].insert(
            buildJsonObject {
                put("session_id", point.sessionId)
                put("latitude", point.latitude)
                put("longitude", point.longitude)
                put("accuracy", point.accuracy)
                put("altitude", point.altitude)
                put("speed", point.speed)
                put("recorded_at", point.recordedAt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
            }
        )
    }
}

@Serializable
data class LocationSessionDto(
    val id: String,
    @SerialName("task_id") val taskId: String,
    @SerialName("employee_id") val employeeId: String,
    val status: String,
    @SerialName("started_at") val startedAt: String,
    @SerialName("ended_at") val endedAt: String? = null
) {
    fun toDomain() = LocationSession(
        id = id,
        taskId = taskId,
        employeeId = employeeId,
        status = status,
        startedAt = OffsetDateTime.parse(startedAt),
        endedAt = endedAt?.let { OffsetDateTime.parse(it) }
    )
}
