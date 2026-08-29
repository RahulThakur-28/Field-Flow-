package com.rahul.fieldflow.domain.repository

import com.rahul.fieldflow.domain.model.UserProfile
import com.rahul.fieldflow.domain.model.UserRole
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<UserProfile?>
    
    suspend fun login(email: String, password: String): Result<Unit>
    
    suspend fun register(
        email: String, 
        password: String, 
        fullName: String, 
        role: UserRole,
        phone: String? = null,
        companyName: String? = null
    ): Result<Unit>
    
    suspend fun logout(): Result<Unit>
    
    suspend fun getProfile(userId: String): Result<UserProfile>
    
    suspend fun getCurrentSession(): String? // returns user id if session exists

    fun isEmailVerified(): Boolean
    
    suspend fun refreshProfile(): Result<UserProfile>

    suspend fun resendVerificationEmail(email: String): Result<Unit>

    suspend fun getTeamMembers(workspaceId: String): Result<List<UserProfile>>

    suspend fun updateProfile(fullName: String, phone: String?): Result<Unit>
}
