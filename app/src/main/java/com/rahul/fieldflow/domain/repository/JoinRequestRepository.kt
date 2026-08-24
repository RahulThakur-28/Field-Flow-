package com.rahul.fieldflow.domain.repository

import com.rahul.fieldflow.domain.model.JoinRequest

interface JoinRequestRepository {
    suspend fun submitRequest(workspaceId: String): Result<Unit>
    suspend fun getMyRequests(): Result<List<JoinRequest>>
    suspend fun getPendingRequests(workspaceId: String): Result<List<JoinRequest>>
    suspend fun respondToRequest(requestId: String, approve: Boolean): Result<Unit>
}
