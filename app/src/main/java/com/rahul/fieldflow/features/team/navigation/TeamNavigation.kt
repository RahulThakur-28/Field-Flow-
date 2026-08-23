package com.rahul.fieldflow.features.team.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.rahul.fieldflow.core.navigation.AppRoutes
import com.rahul.fieldflow.features.team.screen.EmployeeDetailsScreen
import com.rahul.fieldflow.features.team.screen.OwnerTeamScreen

fun NavGraphBuilder.teamNavigation(navController: NavController) {
    composable<AppRoutes.Team> {
        OwnerTeamScreen(
            navController = navController,
            onMemberClick = { employeeId: String ->
                navController.navigate(AppRoutes.EmployeeDetails(employeeId))
            }
        )
    }

    composable<AppRoutes.EmployeeDetails> { backStackEntry ->
        val route = backStackEntry.toRoute<AppRoutes.EmployeeDetails>()
        EmployeeDetailsScreen(
            employeeId = route.employeeId,
            onBackClick = { navController.popBackStack() },
            onTaskClick = { taskId: String ->
                navController.navigate(AppRoutes.TaskDetails(taskId))
            }
        )
    }
}
