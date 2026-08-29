package com.rahul.fieldflow.core.navigation

import android.util.Log
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rahul.fieldflow.domain.model.UserRole
import com.rahul.fieldflow.features.auth.navigation.authNavigation
import com.rahul.fieldflow.features.auth.viewmodel.AuthState
import com.rahul.fieldflow.features.auth.viewmodel.AuthViewModel
import com.rahul.fieldflow.features.onboarding.OnboardingScreen
import com.rahul.fieldflow.features.splash.SplashScreen
import com.rahul.fieldflow.features.home.owner.screen.OwnerHomeScreen
import com.rahul.fieldflow.features.home.employee.screen.EmployeeHomeScreen
import com.rahul.fieldflow.features.tasks.navigation.taskNavigation
import com.rahul.fieldflow.features.team.navigation.teamNavigation
import com.rahul.fieldflow.features.reports.navigation.reportsNavigation
import com.rahul.fieldflow.features.analytics.navigation.analyticsNavigation
import com.rahul.fieldflow.features.profile.navigation.profileNavigation

@Composable
fun AppNavGraph(
    navController: NavHostController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()
    val currentAuthState = authState
    var isAnimationComplete by remember { mutableStateOf(false) }

    Log.d("FIELD_FLOW_STARTUP", "AppNavGraph recomposing: authState=$currentAuthState, animComplete=$isAnimationComplete")

    // Decision Logic: When to move from Splash/Auth to Home
    LaunchedEffect(authState, isAnimationComplete) {
        Log.d("FIELD_FLOW_STARTUP", "LaunchedEffect triggered: authState=$currentAuthState, animComplete=$isAnimationComplete")
        
        if (!isAnimationComplete) {
            Log.d("FIELD_FLOW_STARTUP", "Waiting for animation to complete...")
            return@LaunchedEffect
        }

        val currentDestination = navController.currentBackStackEntry?.destination
        val currentRoute = currentDestination?.route
        Log.d("FIELD_FLOW_STARTUP", "Current route: $currentRoute")

        when (currentAuthState) {
            is AuthState.Authenticated,
            is AuthState.EmailUnverified,
            AuthState.NoWorkspace,
            AuthState.PendingApproval,
            AuthState.Rejected -> {
                Log.d("FIELD_FLOW_STARTUP", "Processing state: $currentAuthState")

                val targetDestination: Any = when (currentAuthState) {
                    is AuthState.Authenticated -> {
                        when (currentAuthState.user.role) {
                            UserRole.OWNER -> AppRoutes.OwnerHome
                            UserRole.EMPLOYEE -> AppRoutes.EmployeeHome
                        }
                    }
                    is AuthState.EmailUnverified -> AppRoutes.EmailVerification
                    AuthState.NoWorkspace -> AppRoutes.EmployeeJoin
                    AuthState.PendingApproval -> AppRoutes.JoinRequestSent
                    AuthState.Rejected -> AppRoutes.JoinRequestSent
                    else -> AppRoutes.Login
                }

                val targetRouteName = targetDestination::class.qualifiedName ?: ""
                Log.d("FIELD_FLOW_STARTUP", "Target route name: $targetRouteName")

                // Only navigate if we are in an "auth" destination (Splash, Onboarding, Login etc.)
                // OR if we are not already at the target destination
                if (isAuthDestination(currentDestination) || (currentRoute != null && !currentRoute.contains(targetRouteName))) {

                    // Special case: if we are already at the correct sub-onboarding screen, don't re-navigate
                    if (currentRoute != null && currentRoute.contains(targetRouteName)) {
                         Log.d("FIELD_FLOW_STARTUP", "Already at target destination: $currentRoute")
                         return@LaunchedEffect
                    }

                    Log.d("FIELD_FLOW_STARTUP", "Navigating to: $targetDestination")
                    navController.navigate(targetDestination) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }

            AuthState.Unauthenticated -> {
                if (!isAuthDestination(currentDestination)) {
                    Log.d("FIELD_FLOW_STARTUP", "Not in auth destination, redirecting to Login")
                    navController.navigate(AppRoutes.Login) {
                        popUpTo(0) { inclusive = true }
                    }
                } else if (currentRoute?.contains("Splash") == true || currentRoute == null) {
                    Log.d("FIELD_FLOW_STARTUP", "On Splash or null, redirecting to Onboarding")
                    navController.navigate(AppRoutes.Onboarding) {
                        popUpTo(AppRoutes.Splash) { inclusive = true }
                    }
                }
            }

            is AuthState.Error -> {
                Log.e("FIELD_FLOW_STARTUP", "AuthState Error: ${currentAuthState.message}")
                if (!currentRoute.toString().contains("Login")) {
                    navController.navigate(AppRoutes.Login) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }

            AuthState.Checking -> {
                Log.d("FIELD_FLOW_STARTUP", "Still checking auth...")
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = AppRoutes.Splash
    ) {
        composable<AppRoutes.Splash> {
            SplashScreen(
                onAnimationComplete = {
                    Log.d("FIELD_FLOW_STARTUP", "Splash animation callback received")
                    isAnimationComplete = true
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

/**
 * Checks if the destination belongs to the authentication or onboarding flow.
 */
private fun isAuthDestination(destination: NavDestination?): Boolean {
    if (destination == null) return true
    val route = destination.route ?: return true
    
    val authRoutes = listOf(
        "Splash",
        "Onboarding",
        "Login",
        "Registration",
        "EmailVerification",
        "EmployeeJoin",
        "CompanyFound",
        "JoinRequestSent",
        "employee_join",
        "company_found"
    )
    
    return authRoutes.any { route.contains(it, ignoreCase = true) }
}
