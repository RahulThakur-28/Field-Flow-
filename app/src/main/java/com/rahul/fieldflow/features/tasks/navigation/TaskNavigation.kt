package com.rahul.fieldflow.features.tasks.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.rahul.fieldflow.core.navigation.AppRoutes
import com.rahul.fieldflow.features.tasks.employee.screen.*
import com.rahul.fieldflow.features.tasks.model.SelectedLocation
import com.rahul.fieldflow.features.tasks.owner.screen.*
import org.maplibre.android.geometry.LatLng

fun NavGraphBuilder.taskNavigation(navController: NavController) {
    navigation<AppRoutes.Tasks>(startDestination = AppRoutes.OwnerTasks()) {
        // Owner Routes
        composable<AppRoutes.OwnerTasks> {
            OwnerTasksScreen(
                navController = navController,
                onTaskClick = { taskId ->
                    navController.navigate(AppRoutes.TaskDetails(taskId))
                },
                onCreateTaskClick = {
                    navController.navigate(AppRoutes.CreateTask)
                }
            )
        }

        composable<AppRoutes.CreateTask> { backStackEntry ->
            val result = backStackEntry.savedStateHandle.get<SelectedLocation>("selected_location")
            CreateTaskScreen(
                onBackClick = { navController.popBackStack() },
                onTaskCreated = { navController.popBackStack() },
                onPickOnMap = { lat, lng, radius ->
                    navController.navigate(AppRoutes.LocationPicker(lat, lng, radius))
                },
                selectedLocation = result
            )
        }

        composable<AppRoutes.LocationPicker> { backStackEntry ->
            val route = backStackEntry.toRoute<AppRoutes.LocationPicker>()
            val initialLatLng = if (route.initialLat != null && route.initialLng != null) {
                LatLng(route.initialLat, route.initialLng)
            } else null

            LocationPickerScreen(
                initialLocation = initialLatLng,
                initialRadius = route.initialRadius,
                onBackClick = { navController.popBackStack() },
                onLocationConfirm = { location ->
                    navController.previousBackStackEntry?.savedStateHandle?.set("selected_location", location)
                    navController.popBackStack()
                }
            )
        }

        composable<AppRoutes.TaskDetails> { backStackEntry ->
            val route = backStackEntry.toRoute<AppRoutes.TaskDetails>()
            OwnerTaskDetailsScreen(
                taskId = route.taskId,
                onBackClick = { navController.popBackStack() },
                onTrackClick = { taskId ->
                    navController.navigate(AppRoutes.LiveTracking(taskId))
                },
                onViewReportClick = { taskId ->
                    navController.navigate(AppRoutes.TaskReport(taskId))
                }
            )
        }

        composable<AppRoutes.EditTask> { backStackEntry ->
            val route = backStackEntry.toRoute<AppRoutes.EditTask>()
            val result = backStackEntry.savedStateHandle.get<SelectedLocation>("selected_location")
            EditTaskScreen(
                taskId = route.taskId,
                onBackClick = { navController.popBackStack() },
                onTaskUpdated = { navController.popBackStack() },
                onPickOnMap = { lat, lng, radius ->
                    navController.navigate(AppRoutes.LocationPicker(lat, lng, radius))
                },
                selectedLocation = result
            )
        }

        composable<AppRoutes.LiveTracking> { backStackEntry ->
            val route = backStackEntry.toRoute<AppRoutes.LiveTracking>()
            OwnerLiveTrackingScreen(
                taskId = route.taskId,
                onBackClick = { navController.popBackStack() }
            )
        }

        // Employee Routes
        composable<AppRoutes.EmployeeTasks> {
            EmployeeTasksScreen(
                navController = navController,
                onTaskClick = { taskId ->
                    navController.navigate(AppRoutes.EmployeeTaskDetails(taskId))
                }
            )
        }

        composable<AppRoutes.EmployeeTaskDetails> { backStackEntry ->
            val route = backStackEntry.toRoute<AppRoutes.EmployeeTaskDetails>()
            EmployeeTaskDetailsScreen(
                taskId = route.taskId,
                onBackClick = { navController.popBackStack() },
                onViewReportClick = { taskId ->
                    navController.navigate(AppRoutes.TaskReport(taskId))
                }
            )
        }
    }
}
