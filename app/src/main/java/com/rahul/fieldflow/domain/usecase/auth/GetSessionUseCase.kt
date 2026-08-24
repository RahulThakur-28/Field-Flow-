package com.rahul.fieldflow.domain.usecase.auth

import com.rahul.fieldflow.domain.repository.AuthRepository
import javax.inject.Inject

class GetSessionUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): String? {
        return authRepository.getCurrentSession()
    }
}
