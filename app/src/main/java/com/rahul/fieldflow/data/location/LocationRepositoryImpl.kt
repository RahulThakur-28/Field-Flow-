package com.rahul.fieldflow.data.location

import com.rahul.fieldflow.data.auth.AuthDataSource
import com.rahul.fieldflow.domain.model.LocationPoint
import com.rahul.fieldflow.domain.model.LocationSession
import com.rahul.fieldflow.domain.repository.LocationRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepositoryImpl @Inject constructor(
    private val locationDataSource: LocationDataSource,
    private val authDataSource: AuthDataSource
) : LocationRepository {

    override suspend fun startLocationSession(taskId: String): Result<LocationSession> {
        return runCatching {
            val employeeId = authDataSource.getCurrentUserId() ?: throw Exception("Not logged in")
            
            // Check for existing active session
            val activeSession = locationDataSource.getActiveSession(taskId, employeeId)
            if (activeSession != null) {
                return@runCatching activeSession.toDomain()
            }
            
            locationDataSource.createSession(taskId, employeeId).toDomain()
        }
    }

    override suspend fun getActiveSession(taskId: String): Result<LocationSession?> {
        return runCatching {
            val employeeId = authDataSource.getCurrentUserId() ?: throw Exception("Not logged in")
            locationDataSource.getActiveSession(taskId, employeeId)?.toDomain()
        }
    }

    override suspend fun stopLocationSession(sessionId: String): Result<Unit> {
        return runCatching {
            locationDataSource.updateSessionEnd(sessionId)
        }
    }

    override suspend fun recordLocationPoint(point: LocationPoint): Result<Unit> {
        return runCatching {
            locationDataSource.insertPoint(point)
        }
    }
}
