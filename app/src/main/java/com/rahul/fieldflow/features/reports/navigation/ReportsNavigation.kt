package com.rahul.fieldflow.features.reports.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.rahul.fieldflow.core.navigation.AppRoutes
import com.rahul.fieldflow.features.reports.owner.screen.OwnerReportsScreen
import com.rahul.fieldflow.features.reports.owner.screen.OwnerReportDetailsScreen
import com.rahul.fieldflow.features.reports.employee.screen.EmployeeReportsScreen
import com.rahul.fieldflow.features.reports.employee.screen.EmployeeReportDetailsScreen
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
        OwnerReportsScreen(
            navController = navController,
            onReportClick = { reportId: String ->
                navController.navigate(AppRoutes.OwnerReportDetails(reportId))
            }
        )
    }

    composable<AppRoutes.EmployeeReports> {
        EmployeeReportsScreen(
            navController = navController,
            onReportClick = { reportId: String ->
                navController.navigate(AppRoutes.EmployeeReportDetails(reportId))
            }
        )
    }

    composable<AppRoutes.OwnerReportDetails> { backStackEntry ->
        val route = backStackEntry.toRoute<AppRoutes.OwnerReportDetails>()
        OwnerReportDetailsScreen(
            reportId = route.reportId,
            onBackClick = { navController.popBackStack() }
        )
    }

    composable<AppRoutes.EmployeeReportDetails> { backStackEntry ->
        val route = backStackEntry.toRoute<AppRoutes.EmployeeReportDetails>()
        EmployeeReportDetailsScreen(
            reportId = route.reportId,
            onBackClick = { navController.popBackStack() }
        )
    }
}
