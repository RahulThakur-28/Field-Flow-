package com.rahul.fieldflow.features.home.employee.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.rahul.fieldflow.core.navigation.AppRoutes
import com.rahul.fieldflow.features.bottomnavigation.components.FieldFlowBottomNavigation
import com.rahul.fieldflow.features.bottomnavigation.navigation.BottomNavigationConfig
import com.rahul.fieldflow.features.home.components.FieldFlowHeader
import com.rahul.fieldflow.features.home.components.SectionHeader
import com.rahul.fieldflow.features.home.components.SummaryStatCard
import com.rahul.fieldflow.features.home.employee.components.NextTaskCard
import com.rahul.fieldflow.features.home.employee.components.QuickAccessCard
import com.rahul.fieldflow.features.home.employee.components.ScheduleTaskCard
import com.rahul.fieldflow.features.home.employee.state.EmployeeHomeUiState
import com.rahul.fieldflow.features.home.employee.viewmodel.EmployeeHomeViewModel
import com.rahul.fieldflow.features.home.model.dummyEmployeeHomeUiState
import com.rahul.fieldflow.ui.theme.BackgroundLight
import com.rahul.fieldflow.ui.theme.FieldFlowTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeHomeScreen(
    navController: NavController,
    viewModel: EmployeeHomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    PullToRefreshBox(
        isRefreshing = uiState.isLoading,
        onRefresh = { viewModel.refresh() }
    ) {
        EmployeeHomeContent(
            uiState = uiState,
            navController = navController
        )
    }
}

@Composable
fun EmployeeHomeContent(
    uiState: EmployeeHomeUiState,
    navController: NavController
) {
    Scaffold(
        containerColor = BackgroundLight,
        bottomBar = {
            FieldFlowBottomNavigation(
                items = BottomNavigationConfig.employeeItems,
                navController = navController
            )
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {

            /*
             * Header
             */
            item {
                FieldFlowHeader(
                    date = uiState.date,
                    userName = uiState.userName,
                    subtitle = "Ready for today's work?",
                    initials = uiState.initials,
                    notificationCount = uiState.notificationCount,
                    onProfileClick = { navController.navigate(AppRoutes.EmployeeProfile) },
                    onNotificationClick = { /* Handle notifications */ }
                )
            }

            /*
             * Today's Summary
             */
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    uiState.stats.forEach { stat ->
                        SummaryStatCard(
                            stat = stat,
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigate(AppRoutes.EmployeeTasks) }
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(24.dp)
                )
            }

            /*
             * Next Task
             */
            item {
                uiState.nextTask?.let { task ->
                    NextTaskCard(
                        task = task,
                        onClick = {
                            navController.navigate(AppRoutes.EmployeeTasks)
                        }
                    )

                    Spacer(
                        modifier = Modifier.height(24.dp)
                    )
                }
            }

            /*
             * Today's Schedule
             */
            item {
                SectionHeader(
                    title = "Today's Schedule",
                    actionText = "All Tasks",
                    onActionClick = { navController.navigate(AppRoutes.EmployeeTasks) }
                )
            }

            items(
                items = uiState.schedule
            ) { task ->

                ScheduleTaskCard(
                    task = task,
                    onClick = {
                        navController.navigate(AppRoutes.EmployeeTasks)
                    }
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )
            }

            /*
             * Quick Access
             */
            item {
                SectionHeader(
                    title = "Quick Access"
                )
            }

            items(
                items = uiState.quickAccess
            ) { quickAccessItem ->

                QuickAccessCard(
                    item = quickAccessItem,
                    onClick = {
                        when (quickAccessItem.title) {
                            "My Tasks" -> navController.navigate(AppRoutes.EmployeeTasks)
                            "Reports" -> navController.navigate(AppRoutes.EmployeeReports)
                        }
                    }
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )
            }

            /*
             * Bottom spacing
             */
            item {
                Spacer(
                    modifier = Modifier.height(32.dp)
                )
            }
        }
    }
}@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun EmployeeHomeScreenPreview() {
    FieldFlowTheme {
        EmployeeHomeContent(
            uiState = dummyEmployeeHomeUiState(),
            navController = rememberNavController()
        )
    }
}
