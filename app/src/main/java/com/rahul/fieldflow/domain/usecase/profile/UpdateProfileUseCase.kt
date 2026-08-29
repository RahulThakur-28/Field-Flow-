package com.rahul.fieldflow.domain.usecase.profile

import com.rahul.fieldflow.domain.repository.AuthRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(fullName: String, phone: String?): Result<Unit> {
        return authRepository.updateProfile(fullName, phone)
    }
}
