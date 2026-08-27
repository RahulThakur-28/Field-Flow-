package com.rahul.fieldflow.domain.repository

import com.rahul.fieldflow.domain.model.LocationPoint
import com.rahul.fieldflow.domain.model.LocationSession

interface LocationRepository {
    suspend fun startLocationSession(taskId: String): Result<LocationSession>
    suspend fun getActiveSession(taskId: String): Result<LocationSession?>
    suspend fun stopLocationSession(sessionId: String): Result<Unit>
    suspend fun recordLocationPoint(point: LocationPoint): Result<Unit>
}
