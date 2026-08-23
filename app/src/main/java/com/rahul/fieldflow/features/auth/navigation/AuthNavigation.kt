package com.rahul.fieldflow.features.auth.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.rahul.fieldflow.core.navigation.AppRoutes
import com.rahul.fieldflow.features.auth.common.screen.*
import com.rahul.fieldflow.features.auth.owner.screen.*
import com.rahul.fieldflow.features.auth.employee.screen.*
import com.rahul.fieldflow.domain.model.UserRole

fun NavGraphBuilder.authNavigation(navController: NavController) {

    // For simplicity in this demo/refactor, using a simulated role here as well
    // or it could be passed in. The AppNavGraph currently has its own logic.
    val simulatedRole = UserRole.EMPLOYEE

    composable<AppRoutes.Login> {
        LoginScreen(
            onNavigateToRegister = { navController.navigate(AppRoutes.RoleSelection) },
            onLoginSuccess = {
                val destination = if (simulatedRole == UserRole.OWNER) AppRoutes.OwnerHome else AppRoutes.EmployeeHome
                navController.navigate(destination) {
                    popUpTo(AppRoutes.Login) { inclusive = true }
                }
            }
        )
    }

    composable<AppRoutes.RoleSelection> {
        RoleSelectionScreen(
            onBack = { navController.popBackStack() },
            onNavigateToOwnerReg = { navController.navigate(AppRoutes.OwnerRegistration) },
            onNavigateToEmployeeJoin = { navController.navigate(AppRoutes.EmployeeJoin) }
        )
    }

    composable<AppRoutes.OwnerRegistration> {
        OwnerRegistrationScreen(
            onBack = { navController.popBackStack() },
            onSuccess = { navController.navigate(AppRoutes.CompanyCreated) }
        )
    }

    composable<AppRoutes.CompanyCreated> {
        CompanyCreatedScreen(
            onContinue = { navController.navigate(AppRoutes.EmailVerification) }
        )
    }

    composable<AppRoutes.EmployeeJoin> {
        EmployeeJoinScreen(
            onBack = { navController.popBackStack() },
            onCompanyFound = { navController.navigate(AppRoutes.CompanyFound) },
            onNavigateToInvitation = { navController.navigate(AppRoutes.EmployeeInvitation) }
        )
    }

    composable<AppRoutes.CompanyFound> {
        CompanyFoundScreen(
            onBack = { navController.popBackStack() },
            onSendRequest = { navController.navigate(AppRoutes.JoinRequestSent) }
        )
    }

    composable<AppRoutes.JoinRequestSent> {
        JoinRequestSentScreen(
            onBackToLogin = { 
                navController.navigate(AppRoutes.Login) { 
                    popUpTo(AppRoutes.Login) { inclusive = true } 
                } 
            }
        )
    }

    composable<AppRoutes.EmployeeInvitation> {
        EmployeeInvitationScreen(
            onAccept = { navController.navigate(AppRoutes.EmployeeRegistration) },
            onDecline = { navController.popBackStack() }
        )
    }

    composable<AppRoutes.EmployeeRegistration> {
        EmployeeRegistrationScreen(
            onBack = { navController.popBackStack() },
            onSuccess = { navController.navigate(AppRoutes.EmailVerification) }
        )
    }

    composable<AppRoutes.EmailVerification> {
        EmailVerificationScreen(
            onVerifySuccess = {
                val destination = if (simulatedRole == UserRole.OWNER) AppRoutes.OwnerHome else AppRoutes.EmployeeHome
                navController.navigate(destination) {
                    popUpTo(AppRoutes.Login) { inclusive = true }
                }
            },
            onChangeEmail = { navController.popBackStack() }
        )
    }
}
