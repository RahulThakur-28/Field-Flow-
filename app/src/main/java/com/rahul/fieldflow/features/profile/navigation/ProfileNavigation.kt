package com.rahul.fieldflow.features.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.rahul.fieldflow.core.navigation.AppRoutes
import com.rahul.fieldflow.features.notifications.screen.NotificationScreen
import com.rahul.fieldflow.features.profile.owner.screen.*
import com.rahul.fieldflow.features.profile.employee.screen.*

fun NavGraphBuilder.profileNavigation(navController: NavController) {
    composable<AppRoutes.OwnerProfile> {
        OwnerProfileScreen(navController = navController)
    }

    composable<AppRoutes.EmployeeProfile> {
        EmployeeProfileScreen(navController = navController)
    }

    composable<AppRoutes.ChangePassword> {
        ChangePasswordScreen(navController = navController)
    }

    composable<AppRoutes.NotificationSettings> {
        NotificationSettingsScreen(navController = navController)
    }

    composable<AppRoutes.AppSettings> {
        AppSettingsScreen(navController = navController)
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

    composable<AppRoutes.Notifications> {
        NotificationScreen(onBack = { navController.popBackStack() })
    }

    composable<AppRoutes.AboutUs> {
        AboutUsScreen(onBack = { navController.popBackStack() })
    }

    composable<AppRoutes.PrivacyPolicy> {
        PrivacyPolicyScreen(onBack = { navController.popBackStack() })
    }
}
