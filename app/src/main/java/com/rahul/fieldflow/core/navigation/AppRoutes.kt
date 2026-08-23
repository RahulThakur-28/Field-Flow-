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
    data object OwnerHome : AppRoutes()
    
    @Serializable
    data object EmployeeHome : AppRoutes()

    @Serializable
    data object OwnerTasks : AppRoutes()

    @Serializable
    data object EmployeeTasks : AppRoutes()

    @Serializable
    data object CreateTask : AppRoutes()

    @Serializable
    data class TaskDetails(val taskId: String) : AppRoutes()

    @Serializable
    data class EmployeeTaskDetails(val taskId: String) : AppRoutes()

    @Serializable
    data class EditTask(val taskId: String) : AppRoutes()

    @Serializable
    data class LiveTracking(val taskId: String) : AppRoutes()

    @Serializable
    data object Tasks : AppRoutes()

    @Serializable
    data class EmployeeDetails(val employeeId: String) : AppRoutes()

    @Serializable
    data object Team : AppRoutes()

    @Serializable
    data class ReportDetails(val reportId: String) : AppRoutes()

    @Serializable
    data object Reports : AppRoutes()

    @Serializable
    data object Analytics : AppRoutes()

    @Serializable
    data object Profile : AppRoutes()
}
