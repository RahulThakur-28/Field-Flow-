package com.rahul.fieldflow.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rahul.fieldflow.features.onboarding.OnboardingScreen
import com.rahul.fieldflow.features.splash.SplashScreen
import com.rahul.fieldflow.features.home.owner.screen.OwnerHomeScreen
import com.rahul.fieldflow.features.home.employee.screen.EmployeeHomeScreen
import com.rahul.fieldflow.features.tasks.navigation.taskNavigation
import com.rahul.fieldflow.features.team.navigation.teamNavigation
import com.rahul.fieldflow.features.reports.navigation.reportsNavigation
import com.rahul.fieldflow.features.analytics.navigation.analyticsNavigation
import com.rahul.fieldflow.features.profile.navigation.profileNavigation
import com.rahul.fieldflow.features.auth.navigation.authNavigation

@Composable
fun AppNavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = AppRoutes.Splash
    ) {
        composable<AppRoutes.Splash> {
            SplashScreen(
                onAnimationComplete = {
                    navController.navigate(AppRoutes.Onboarding) {
                        popUpTo(AppRoutes.Splash) { inclusive = true }
                    }
                }
            )
        }
        
        composable<AppRoutes.Onboarding> {
            OnboardingScreen(
                onFinish = {
                    navController.navigate(AppRoutes.Login) {
                        popUpTo(AppRoutes.Onboarding) { inclusive = true }
                    }
                }
            )
        }

        composable<AppRoutes.OwnerHome> {
            OwnerHomeScreen(navController = navController)
        }

        composable<AppRoutes.EmployeeHome> {
            EmployeeHomeScreen(navController = navController)
        }

        authNavigation(navController)
        taskNavigation(navController)
        teamNavigation(navController)
        reportsNavigation(navController)
        analyticsNavigation(navController)
        profileNavigation(navController)
    }
}
