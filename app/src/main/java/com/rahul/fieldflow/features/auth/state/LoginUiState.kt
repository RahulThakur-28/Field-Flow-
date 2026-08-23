package com.rahul.fieldflow.features.auth.state

data class LoginUiState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val loginSuccess: Boolean = false,
    val error: String? = null
)
