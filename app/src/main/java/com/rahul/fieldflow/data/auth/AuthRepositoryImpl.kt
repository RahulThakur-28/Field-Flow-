package com.rahul.fieldflow.data.auth

import android.util.Log
import com.rahul.fieldflow.domain.model.UserProfile
import com.rahul.fieldflow.domain.model.UserRole
import com.rahul.fieldflow.domain.repository.AuthRepository
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

import kotlinx.coroutines.withTimeoutOrNull

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource
) : AuthRepository {

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    override val currentUser: Flow<UserProfile?> = _currentUser.asStateFlow()

    init {
        observeSessionStatus()
    }

    private fun observeSessionStatus() {
        authDataSource.sessionStatus
            .onEach { status ->
                when (status) {
                    is SessionStatus.Authenticated -> {
                        val userId = status.session.user?.id
                        if (userId != null) {
                            refreshProfile()
                        }
                    }
                    is SessionStatus.NotAuthenticated -> {
                        _currentUser.value = null
                    }
                    else -> {}
                }
            }
            .launchIn(repositoryScope)
    }

    override suspend fun login(
        email: String,
        password: String
    ): Result<Unit> {
        return runCatching {
            Log.d("LOGIN_DEBUG", "1. Starting Supabase Auth login")

            authDataSource.login(email, password)

            Log.d("LOGIN_DEBUG", "2. Supabase Auth login SUCCESS")

            val userId = authDataSource.getCurrentUserId()

            Log.d(
                "LOGIN_DEBUG",
                "3. Current user ID = $userId"
            )

            val actualUserId = userId
                ?: throw Exception("Login failed: User ID is null")

            Log.d(
                "LOGIN_DEBUG",
                "4. Loading profile..."
            )

            val profile = withTimeoutOrNull(10_000) {
                authDataSource
                    .getProfile(actualUserId)
                    .toDomain()
            } ?: throw Exception("Profile load timeout")

            Log.d(
                "LOGIN_DEBUG",
                "5. Profile loaded: ${profile.email}"
            )

            Log.d(
                "LOGIN_DEBUG",
                "6. Role: ${profile.role}"
            )

            Log.d(
                "LOGIN_DEBUG",
                "7. Workspace: ${profile.workspaceId}"
            )

            _currentUser.value = profile

            Log.d(
                "LOGIN_DEBUG",
                "8. Login repository SUCCESS"
            )

            // IMPORTANT:
            // runCatching must return Result<Unit>
            Unit

        }.onFailure { error ->
            Log.e(
                "LOGIN_DEBUG",
                "LOGIN FAILED",
                error
            )
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        fullName: String,
        role: UserRole,
        phone: String?,
        companyName: String?
    ): Result<Unit> {
        return runCatching {
            authDataSource.register(
                email = email,
                password = password,
                fullName = fullName,
                role = role.name.lowercase(),
                phone = phone
            )
            // Note: In a real app, you might wait for email verification
            // For now, if auto-confirm is on, we try to fetch the profile
            val userId = authDataSource.getCurrentUserId()
            if (userId != null) {
                try {
                    val profile = withTimeoutOrNull(5000) {
                        authDataSource.getProfile(userId).toDomain()
                    }
                    if (profile != null) {
                        _currentUser.value = profile
                    }
                } catch (e: Exception) {
                    // Profile might not be created yet if trigger is slow or email not verified
                }
            }
        }
    }

    override suspend fun logout(): Result<Unit> {
        return runCatching {
            authDataSource.logout()
            _currentUser.value = null
        }
    }

    override suspend fun getProfile(userId: String): Result<UserProfile> {
        return runCatching {
            withTimeoutOrNull(10000) {
                authDataSource.getProfile(userId).toDomain()
            } ?: throw Exception("Profile load timeout")
        }
    }

    override suspend fun getCurrentSession(): String? {
        val userId = authDataSource.getCurrentUserId()
        if (userId != null && _currentUser.value == null) {
            // Restore profile if session exists but memory is cleared
            getProfile(userId).onSuccess {
                _currentUser.value = it
            }
        }
        return userId
    }

    override fun isEmailVerified(): Boolean {
        return authDataSource.isEmailVerified()
    }

    override suspend fun refreshProfile(): Result<UserProfile> {
        return runCatching {
            val userId = authDataSource.getCurrentUserId() ?: throw Exception("No active session")
            val profile = authDataSource.getProfile(userId).toDomain()
            _currentUser.value = profile
            profile
        }
    }

    override suspend fun resendVerificationEmail(email: String): Result<Unit> {
        return runCatching {
            authDataSource.resendEmail(email)
        }
    }

    override suspend fun getTeamMembers(workspaceId: String): Result<List<UserProfile>> {
        return runCatching {
            authDataSource.getTeamMembers(workspaceId).map { it.toDomain() }
        }
    }
}
