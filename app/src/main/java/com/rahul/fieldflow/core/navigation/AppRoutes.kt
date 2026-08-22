package com.rahul.fieldflow.core.navigation

import kotlinx.serialization.Serializable

sealed class AppRoutes {
    @Serializable
    data object Splash : AppRoutes()
    
    @Serializable
    data object Onboarding : AppRoutes()
    
    @Serializable
    data object Home : AppRoutes()
}
