package com.rahul.fieldflow.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val id: String,
    val fullName: String,
    val email: String,
    val phone: String? = null,
    val role: UserRole,
    val employeeCode: String? = null,
    val avatarUrl: String? = null,
    val isActive: Boolean = true,
    val workspaceId: String? = null,
    val createdAt: String? = null
)
