package com.rahul.fieldflow.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rahul.fieldflow.features.splash.SplashScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = AppRoutes.Splash
    ) {
        composable<AppRoutes.Splash> {
            SplashScreen(
                onAnimationComplete = {
                    navController.navigate(AppRoutes.Home) {
                        popUpTo(AppRoutes.Splash) { inclusive = true }
                    }
                }
            )
        }
        
        composable<AppRoutes.Home> {
            // TODO: Replace with actual Home screen
        }
    }
}
