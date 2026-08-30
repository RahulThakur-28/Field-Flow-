package com.rahul.fieldflow.features.reports.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.rahul.fieldflow.core.navigation.AppRoutes
import com.rahul.fieldflow.features.reports.owner.screen.OwnerReportsScreen
import com.rahul.fieldflow.features.reports.employee.screen.EmployeeReportsScreen
import com.rahul.fieldflow.features.reports.screen.TaskReportScreen

fun NavGraphBuilder.reportsNavigation(navController: NavController) {
    composable<AppRoutes.TaskReport> { backStackEntry ->
        val route = backStackEntry.toRoute<AppRoutes.TaskReport>()
        TaskReportScreen(
            taskId = route.taskId,
            onBackClick = { navController.popBackStack() }
        )
    }

    composable<AppRoutes.OwnerReports> {
        LaunchedEffect(Unit) {
            navController.navigate(AppRoutes.OwnerHome(initialPage = 3)) {
                popUpTo(AppRoutes.OwnerHome()) { inclusive = true }
            }
        }
    }

    composable<AppRoutes.EmployeeReports> {
        LaunchedEffect(Unit) {
            navController.navigate(AppRoutes.EmployeeHome(initialPage = 2)) {
                popUpTo(AppRoutes.EmployeeHome()) { inclusive = true }
            }
        }
    }
}
