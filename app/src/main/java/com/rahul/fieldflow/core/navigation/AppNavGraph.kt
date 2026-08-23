package com.rahul.fieldflow.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rahul.fieldflow.features.onboarding.OnboardingScreen
import com.rahul.fieldflow.features.splash.SplashScreen
import com.rahul.fieldflow.features.auth.*
import com.rahul.fieldflow.features.auth.Login.LoginScreen
import com.rahul.fieldflow.features.auth.Registeration.EmployeeRegistrationScreen
import com.rahul.fieldflow.features.home.owner.screen.OwnerHomeScreen
import com.rahul.fieldflow.features.home.employee.screen.EmployeeHomeScreen
import com.rahul.fieldflow.domain.model.UserRole
import com.rahul.fieldflow.features.tasks.navigation.taskNavigation
import com.rahul.fieldflow.features.team.navigation.teamNavigation
import com.rahul.fieldflow.features.reports.navigation.reportsNavigation
import com.rahul.fieldflow.features.analytics.navigation.analyticsNavigation
import com.rahul.fieldflow.features.profile.navigation.profileNavigation

@Composable
fun AppNavGraph(navController: NavHostController) {

    val simulatedRole = UserRole.EMPLOYEE

    NavHost(
        navController = navController,
        startDestination =AppRoutes.EmployeeHome
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
                onBackToLogin = { navController.navigate(AppRoutes.Login) { popUpTo(AppRoutes.Login) { inclusive = true } } }
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
        
        composable<AppRoutes.OwnerHome> {
            OwnerHomeScreen(navController = navController)
        }

        composable<AppRoutes.EmployeeHome> {
            EmployeeHomeScreen(navController = navController)
        }

        taskNavigation(navController)
        teamNavigation(navController)
        reportsNavigation(navController)
        analyticsNavigation(navController)
        profileNavigation(navController)
    }
}
