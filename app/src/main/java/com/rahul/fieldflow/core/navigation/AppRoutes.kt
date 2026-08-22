package com.rahul.fieldflow.core.navigation

import kotlinx.serialization.Serializable

sealed class AppRoutes {
    @Serializable
    data object Splash : AppRoutes()
    
    @Serializable
    data object Onboarding : AppRoutes()
    
    @Serializable
    data object Login : AppRoutes()
    
    @Serializable
    data object RoleSelection : AppRoutes()
    
    @Serializable
    data object OwnerRegistration : AppRoutes()
    
    @Serializable
    data object CompanyCreated : AppRoutes()
    
    @Serializable
    data object EmployeeJoin : AppRoutes()
    
    @Serializable
    data object CompanyFound : AppRoutes()
    
    @Serializable
    data object JoinRequestSent : AppRoutes()
    
    @Serializable
    data object EmployeeInvitation : AppRoutes()
    
    @Serializable
    data object EmployeeRegistration : AppRoutes()
    
    @Serializable
    data object EmailVerification : AppRoutes()
    
    @Serializable
    data object Home : AppRoutes()
}
