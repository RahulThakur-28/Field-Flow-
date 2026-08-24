package com.rahul.fieldflow.data.auth

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject

class AuthDataSource @Inject constructor(
    private val supabaseClient: SupabaseClient
) {
    val sessionStatus: Flow<SessionStatus> = supabaseClient.auth.sessionStatus

    suspend fun login(email: String, password: String) {
        supabaseClient.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun register(
        email: String,
        password: String,
        fullName: String,
        role: String,
        phone: String? = null
    ) {
        supabaseClient.auth.signUpWith(
            provider = Email,
            redirectUrl = "fieldflow://auth/callback"
        ) {
            this.email = email
            this.password = password
            data = buildJsonObject {
                put("full_name", fullName)
                if (phone != null) put("phone", phone)
                put("role", role)
            }
        }
    }

    suspend fun resendEmail(email: String) {
        supabaseClient.auth.resendEmail(
            type = io.github.jan.supabase.auth.OtpType.Email.SIGNUP,
            email = email,
            redirectUrl = "fieldflow://auth/callback"
        )
    }

    suspend fun logout() {
        supabaseClient.auth.signOut()
    }

    suspend fun getProfile(userId: String): ProfileDto {
        return supabaseClient.postgrest["profiles"]
            .select {
                filter {
                    eq("id", userId)
                }
            }
            .decodeSingle<ProfileDto>()
    }

    suspend fun getTeamMembers(workspaceId: String): List<ProfileDto> {
        return supabaseClient.postgrest["profiles"]
            .select {
                filter {
                    eq("workspace_id", workspaceId)
                }
            }
            .decodeList<ProfileDto>()
    }

    fun getCurrentUserId(): String? {
        return supabaseClient.auth.currentUserOrNull()?.id
    }

    @OptIn(io.github.jan.supabase.annotations.SupabaseInternal::class, kotlin.time.ExperimentalTime::class)
    fun isEmailVerified(): Boolean {
        return supabaseClient.auth.currentUserOrNull()?.emailConfirmedAt != null
    }
}
