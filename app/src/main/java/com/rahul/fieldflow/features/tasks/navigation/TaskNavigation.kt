package com.rahul.fieldflow.features.tasks.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.rahul.fieldflow.core.navigation.AppRoutes
import com.rahul.fieldflow.features.tasks.owner.screen.*

fun NavGraphBuilder.taskNavigation(navController: NavController) {
    navigation<AppRoutes.Tasks>(startDestination = AppRoutes.OwnerTasks) {
        composable<AppRoutes.OwnerTasks> {
            OwnerTasksScreen(
                onTaskClick = { taskId ->
                    navController.navigate(AppRoutes.TaskDetails(taskId))
                },
                onCreateTaskClick = {
                    navController.navigate(AppRoutes.CreateTask)
                }
            )
        }

        composable<AppRoutes.CreateTask> {
            CreateTaskScreen(
                onBackClick = { navController.popBackStack() },
                onTaskCreated = { navController.popBackStack() }
            )
        }

        composable<AppRoutes.TaskDetails> { backStackEntry ->
            val route = backStackEntry.toRoute<AppRoutes.TaskDetails>()
            OwnerTaskDetailsScreen(
                taskId = route.taskId,
                onBackClick = { navController.popBackStack() },
                onEditClick = { taskId ->
                    navController.navigate(AppRoutes.EditTask(taskId))
                },
                onTrackClick = { taskId ->
                    navController.navigate(AppRoutes.LiveTracking(taskId))
                }
            )
        }

        composable<AppRoutes.EditTask> { backStackEntry ->
            val route = backStackEntry.toRoute<AppRoutes.EditTask>()
            EditTaskScreen(
                taskId = route.taskId,
                onBackClick = { navController.popBackStack() },
                onTaskUpdated = { navController.popBackStack() }
            )
        }

        composable<AppRoutes.LiveTracking> { backStackEntry ->
            val route = backStackEntry.toRoute<AppRoutes.LiveTracking>()
            OwnerLiveTrackingScreen(
                taskId = route.taskId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
