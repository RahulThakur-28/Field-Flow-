package com.rahul.fieldflow.features.analytics.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.rahul.fieldflow.core.navigation.AppRoutes
import com.rahul.fieldflow.features.analytics.screen.OwnerAnalyticsScreen

fun NavGraphBuilder.analyticsNavigation(navController: NavController) {
    composable<AppRoutes.Analytics> {
        OwnerAnalyticsScreen(navController = navController)
    }
}
