package com.rahul.fieldflow.features.auth.state

data class OwnerRegistrationUiState(
    val fullName: String = "",
    val fullNameError: String? = null,
    val companyName: String = "",
    val companyNameError: String? = null,
    val email: String = "",
    val emailError: String? = null,
    val phone: String = "",
    val password: String = "",
    val passwordError: String? = null,
    val confirmPassword: String = "",
    val confirmPasswordError: String? = null,
    val isLoading: Boolean = false,
    val registrationSuccess: Boolean = false,
    val error: String? = null
)
