package com.rahul.fieldflow.data.requests

import com.rahul.fieldflow.data.auth.AuthDataSource
import com.rahul.fieldflow.domain.model.JoinRequest
import com.rahul.fieldflow.domain.repository.JoinRequestRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JoinRequestRepositoryImpl @Inject constructor(
    private val joinRequestDataSource: JoinRequestDataSource,
    private val authDataSource: AuthDataSource
) : JoinRequestRepository {

    override suspend fun submitRequest(workspaceId: String): Result<Unit> {
        return runCatching {
            val employeeId = authDataSource.getCurrentUserId() ?: throw Exception("Not logged in")
            joinRequestDataSource.submitRequest(employeeId, workspaceId)
        }
    }

    override suspend fun getMyRequests(): Result<List<JoinRequest>> {
        return runCatching {
            val employeeId = authDataSource.getCurrentUserId() ?: throw Exception("Not logged in")
            joinRequestDataSource.getMyRequests(employeeId).map { it.toDomain() }
        }
    }

    override suspend fun getPendingRequests(workspaceId: String): Result<List<JoinRequest>> {
        return runCatching {
            joinRequestDataSource.getPendingRequests(workspaceId).map { it.toDomain() }
        }
    }

    override suspend fun respondToRequest(requestId: String, approve: Boolean): Result<Unit> {
        return runCatching {
            val action = if (approve) "approve" else "reject"
            joinRequestDataSource.respondToRequest(requestId, action)
        }
    }
}
