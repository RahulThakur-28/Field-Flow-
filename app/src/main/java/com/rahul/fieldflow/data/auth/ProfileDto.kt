package com.rahul.fieldflow.data.auth

import com.rahul.fieldflow.domain.model.UserProfile
import com.rahul.fieldflow.domain.model.UserRole
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
    @SerialName("id") val id: String,
    @SerialName("full_name") val fullName: String,
    @SerialName("email") val email: String,
    @SerialName("phone") val phone: String? = null,
    @SerialName("role") val role: String,
    @SerialName("employee_code") val employeeCode: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("workspace_id") val workspaceId: String? = null
) {
    fun toDomain(): UserProfile {
        return UserProfile(
            id = id,
            fullName = fullName,
            email = email,
            phone = phone,
            role = when (role.lowercase()) {
                "owner" -> UserRole.OWNER
                else -> UserRole.EMPLOYEE
            },
            employeeCode = employeeCode,
            avatarUrl = avatarUrl,
            isActive = isActive,
            workspaceId = workspaceId
        )
    }
}
