package com.rahul.fieldflow.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class JoinRequest(
    val id: String,
    val employeeId: String,
    val workspaceId: String,
    val status: JoinRequestStatus,
    val createdAt: String,
    val employeeName: String? = null,
    val employeeEmail: String? = null,
    val employeePhone: String? = null,
    val employeeRole: String? = null,
    val workspaceName: String? = null
)

enum class JoinRequestStatus {
    PENDING, APPROVED, REJECTED
}
