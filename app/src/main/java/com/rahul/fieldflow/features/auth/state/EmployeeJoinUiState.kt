package com.rahul.fieldflow.features.auth.state

data class EmployeeJoinUiState(
    val companyId: String = "",
    val companyIdError: String? = null,
    val isSearching: Boolean = false,
    val companyFound: Boolean = false,
    val foundCompanyName: String = "",
    val foundCompanyId: String = "",
    val workspaceId: String? = null,
    
    val fullName: String = "",
    val fullNameError: String? = null,
    val email: String = "",
    val emailError: String? = null,
    val phone: String = "",
    
    val isLoadingProfile: Boolean = false,
    val isSubmitting: Boolean = false,
    val requestSent: Boolean = false,
    val error: String? = null
)
