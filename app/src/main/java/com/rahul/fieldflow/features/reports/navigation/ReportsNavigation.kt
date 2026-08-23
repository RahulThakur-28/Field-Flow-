package com.rahul.fieldflow.features.reports.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.rahul.fieldflow.core.navigation.AppRoutes
import com.rahul.fieldflow.features.reports.screen.OwnerReportsScreen
import com.rahul.fieldflow.features.reports.screen.ReportDetailsScreen

fun NavGraphBuilder.reportsNavigation(navController: NavController) {
    composable<AppRoutes.Reports> {
        OwnerReportsScreen(
            onReportClick = { reportId: String ->
                navController.navigate(AppRoutes.ReportDetails(reportId))
            }
        )
    }

    composable<AppRoutes.ReportDetails> { backStackEntry ->
        val route = backStackEntry.toRoute<AppRoutes.ReportDetails>()
        ReportDetailsScreen(
            reportId = route.reportId,
            onBackClick = { navController.popBackStack() }
        )
    }
}
