package com.rahul.fieldflow.features.auth.navigation

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.rahul.fieldflow.core.navigation.AppRoutes
import com.rahul.fieldflow.features.auth.common.screen.*
import com.rahul.fieldflow.features.auth.employee.screen.*

fun NavGraphBuilder.authNavigation(navController: NavController) {

    composable<AppRoutes.Login> {
        LoginScreen(
            onNavigateToRegister = { navController.navigate(AppRoutes.EmployeeRegistration) },
            onLoginSuccess = { }
        )
    }

    composable<AppRoutes.EmployeeRegistration> {
        EmployeeRegistrationScreen(
            onBack = { navController.popBackStack() },
            onSuccess = { 
                navController.navigate(AppRoutes.EmailVerification)
            }
        )
    }

    composable<AppRoutes.EmailVerification> {
        EmailVerificationScreen(
            onVerifySuccess = { 
                // Navigation handled by AppNavGraph observing AuthState
            },
            onChangeEmail = { navController.popBackStack() }
        )
    }

    composable<AppRoutes.EmployeeJoin> { backStackEntry ->
        EmployeeJoinScreen(
            onBack = { navController.popBackStack() },
            onCompanyFound = { navController.navigate(AppRoutes.CompanyFound) },
            viewModel = hiltViewModel(backStackEntry)
        )
    }

    composable<AppRoutes.CompanyFound> { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry<AppRoutes.EmployeeJoin>()
        }
        CompanyFoundScreen(
            onBack = { navController.popBackStack() },
            onSendRequest = { navController.navigate(AppRoutes.JoinRequestSent) },
            viewModel = hiltViewModel(parentEntry)
        )
    }

    composable<AppRoutes.JoinRequestSent> {
        JoinRequestSentScreen(
            onBackToLogin = { 
                navController.navigate(AppRoutes.Login) {
                    popUpTo(0) { inclusive = true } 
                } 
            }
        )
    }
}
