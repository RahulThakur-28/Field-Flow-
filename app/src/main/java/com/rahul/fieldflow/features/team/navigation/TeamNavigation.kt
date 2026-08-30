package com.rahul.fieldflow.features.team.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.rahul.fieldflow.core.navigation.AppRoutes
import com.rahul.fieldflow.features.team.screen.EmployeeDetailsScreen
import com.rahul.fieldflow.features.team.screen.EmployeeRequestsScreen
import com.rahul.fieldflow.features.team.screen.OwnerTeamScreen

fun NavGraphBuilder.teamNavigation(navController: NavController) {
    composable<AppRoutes.Team> {
        LaunchedEffect(Unit) {
            navController.navigate(AppRoutes.OwnerHome(initialPage = 2)) {
                popUpTo(AppRoutes.OwnerHome()) { inclusive = true }
            }
        }
    }

    composable<AppRoutes.EmployeeRequests> {
        EmployeeRequestsScreen(
            onBack = { navController.popBackStack() }
        )
    }

    composable<AppRoutes.EmployeeDetails> { backStackEntry ->
        val route = backStackEntry.toRoute<AppRoutes.EmployeeDetails>()
        EmployeeDetailsScreen(
            employeeId = route.employeeId,
            onBackClick = { navController.popBackStack() },
            onTaskClick = { taskId: String ->
                navController.navigate(AppRoutes.TaskDetails(taskId))
            },
            onViewReportClick = { taskId: String ->
                navController.navigate(AppRoutes.TaskReport(taskId))
            }
        )
    }
}
