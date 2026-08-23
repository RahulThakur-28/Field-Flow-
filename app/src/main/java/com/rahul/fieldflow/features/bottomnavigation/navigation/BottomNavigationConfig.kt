package com.rahul.fieldflow.features.bottomnavigation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.rahul.fieldflow.core.navigation.AppRoutes
import com.rahul.fieldflow.features.bottomnavigation.model.NavigationItemUiModel

object BottomNavigationConfig {
    val ownerItems = listOf(
        NavigationItemUiModel("Home", Icons.Default.Home, AppRoutes.OwnerHome),
        NavigationItemUiModel("Tasks", Icons.Default.Assignment, AppRoutes.OwnerTasks),
        NavigationItemUiModel("Team", Icons.Default.Group, AppRoutes.Team),
        NavigationItemUiModel("Reports", Icons.Default.Description, AppRoutes.OwnerReports),
        NavigationItemUiModel("Analytics", Icons.Default.BarChart, AppRoutes.Analytics)
    )

    val employeeItems = listOf(
        NavigationItemUiModel("Home", Icons.Default.Home, AppRoutes.EmployeeHome),
        NavigationItemUiModel("Tasks", Icons.Default.Assignment, AppRoutes.EmployeeTasks),
        NavigationItemUiModel("Reports", Icons.Default.Description, AppRoutes.EmployeeReports),
        NavigationItemUiModel("Profile", Icons.Default.Person, AppRoutes.EmployeeProfile)
    )
}
