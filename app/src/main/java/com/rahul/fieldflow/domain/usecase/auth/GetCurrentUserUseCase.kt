package com.rahul.fieldflow.domain.usecase.auth

import com.rahul.fieldflow.domain.model.UserProfile
import com.rahul.fieldflow.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<UserProfile?> {
        return authRepository.currentUser
    }
}
