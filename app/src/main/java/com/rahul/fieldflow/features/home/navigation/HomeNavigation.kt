package com.rahul.fieldflow.features.home.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.rahul.fieldflow.core.navigation.AppRoutes

data class NavigationItemUiModel(
    val title: String,
    val icon: ImageVector,
    val route: AppRoutes
)

object HomeNavigation {
    val ownerItems = listOf(
        NavigationItemUiModel("Home", Icons.Default.Home, AppRoutes.OwnerHome),
        NavigationItemUiModel("Tasks", Icons.Default.Assignment, AppRoutes.Tasks),
        NavigationItemUiModel("Team", Icons.Default.Group, AppRoutes.Team),
        NavigationItemUiModel("Reports", Icons.Default.Description, AppRoutes.Reports),
        NavigationItemUiModel("Analytics", Icons.Default.BarChart, AppRoutes.Analytics)
    )

    val employeeItems = listOf(
        NavigationItemUiModel("Home", Icons.Default.Home, AppRoutes.EmployeeHome),
        NavigationItemUiModel("Tasks", Icons.Default.Assignment, AppRoutes.EmployeeTasks),
        NavigationItemUiModel("Reports", Icons.Default.Description, AppRoutes.Reports),
        NavigationItemUiModel("Profile", Icons.Default.Person, AppRoutes.Profile)
    )
}
