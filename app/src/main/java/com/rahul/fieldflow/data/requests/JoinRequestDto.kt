package com.rahul.fieldflow.data.requests

import com.rahul.fieldflow.domain.model.JoinRequest
import com.rahul.fieldflow.domain.model.JoinRequestStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JoinRequestDto(
    @SerialName("id") val id: String,
    @SerialName("employee_id") val employeeId: String,
    @SerialName("workspace_id") val workspaceId: String,
    @SerialName("status") val status: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("profiles") val employeeProfile: EmployeeProfileInRequest? = null,
    @SerialName("workspaces") val workspace: WorkspaceInRequest? = null
) {
    fun toDomain() = JoinRequest(
        id = id,
        employeeId = employeeId,
        workspaceId = workspaceId,
        status = when (status.lowercase()) {
            "approved" -> JoinRequestStatus.APPROVED
            "rejected" -> JoinRequestStatus.REJECTED
            else -> JoinRequestStatus.PENDING
        },
        createdAt = createdAt,
        employeeName = employeeProfile?.fullName,
        employeeEmail = employeeProfile?.email,
        employeePhone = employeeProfile?.phone,
        employeeRole = employeeProfile?.role,
        workspaceName = workspace?.name
    )
}

@Serializable
data class EmployeeProfileInRequest(
    @SerialName("full_name") val fullName: String,
    @SerialName("email") val email: String,
    @SerialName("phone") val phone: String? = null,
    @SerialName("role") val role: String
)

@Serializable
data class WorkspaceInRequest(
    @SerialName("name") val name: String
)
