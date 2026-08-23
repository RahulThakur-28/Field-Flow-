package com.rahul.fieldflow.features.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.rahul.fieldflow.core.navigation.AppRoutes
import com.rahul.fieldflow.features.profile.screen.*

fun NavGraphBuilder.profileNavigation(navController: NavController) {
    composable<AppRoutes.Profile> {
        OwnerProfileScreen(navController = navController)
    }

    composable<AppRoutes.OwnerEditProfile> {
        OwnerEditProfileScreen(navController = navController)
    }

    composable<AppRoutes.OwnerNotificationPreferences> {
        OwnerNotificationPreferencesScreen(navController = navController)
    }

    composable<AppRoutes.OwnerAccountSettings> {
        OwnerAccountSettingsScreen(navController = navController)
    }
}
