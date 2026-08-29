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
    data object EmployeeJoin : AppRoutes()
    
    @Serializable
    data object CompanyFound : AppRoutes()
    
    @Serializable
    data object JoinRequestSent : AppRoutes()
    
    @Serializable
    data object EmployeeRegistration : AppRoutes()
    
    @Serializable
    data object EmailVerification : AppRoutes()
    
    @Serializable
    data object OwnerHome : AppRoutes()
    
    @Serializable
    data object EmployeeHome : AppRoutes()

    @Serializable
    data class OwnerTasks(val filter: String? = null) : AppRoutes()

    @Serializable
    data object EmployeeTasks : AppRoutes()

    @Serializable
    data object CreateTask : AppRoutes()

    @Serializable
    data class LocationPicker(
        val initialLat: Double? = null,
        val initialLng: Double? = null,
        val initialRadius: Int = 100
    ) : AppRoutes()

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
    data object EmployeeRequests : AppRoutes()

    @Serializable
    data class TaskReport(val taskId: String) : AppRoutes()

    @Serializable
    data object OwnerReports : AppRoutes()

    @Serializable
    data object EmployeeReports : AppRoutes()

    @Serializable
    data object Analytics : AppRoutes()

    @Serializable
    data object OwnerProfile : AppRoutes()

    @Serializable
    data object EmployeeProfile : AppRoutes()

    @Serializable
    data object ChangePassword : AppRoutes()

    @Serializable
    data object NotificationSettings : AppRoutes()

    @Serializable
    data object AppSettings : AppRoutes()

    @Serializable
    data object OwnerEditProfile : AppRoutes()

    @Serializable
    data object OwnerNotificationPreferences : AppRoutes()

    @Serializable
    data object OwnerAccountSettings : AppRoutes()
}
