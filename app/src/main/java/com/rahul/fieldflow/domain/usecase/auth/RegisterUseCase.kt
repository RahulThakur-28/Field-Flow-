package com.rahul.fieldflow.domain.usecase.auth

import com.rahul.fieldflow.domain.model.UserRole
import com.rahul.fieldflow.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        fullName: String,
        role: UserRole,
        phone: String? = null,
        companyName: String? = null
    ): Result<Unit> {
        return authRepository.register(email, password, fullName, role, phone, companyName)
    }
}
