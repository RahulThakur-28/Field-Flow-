package com.rahul.fieldflow.features.home.employee.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.rahul.fieldflow.core.navigation.AppRoutes
import com.rahul.fieldflow.features.bottomnavigation.components.FieldFlowBottomNavigation
import com.rahul.fieldflow.features.bottomnavigation.navigation.BottomNavigationConfig
import com.rahul.fieldflow.features.home.components.*
import com.rahul.fieldflow.features.home.employee.components.*
import com.rahul.fieldflow.features.home.employee.state.EmployeeHomeUiState
import com.rahul.fieldflow.features.home.employee.viewmodel.EmployeeHomeViewModel
import com.rahul.fieldflow.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeHomeScreen(
    navController: NavController,
    viewModel: EmployeeHomeViewModel = hiltViewModel()
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
        bottomBar = {
            FieldFlowBottomNavigation(
                items = BottomNavigationConfig.employeeItems,
                navController = navController
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                    MaterialTheme.colorScheme.background
                                )
                            )
                        )
                        .padding(horizontal = 20.dp)
                ) {
                    HomeGreetingHeader(
                        userName = uiState.userName,
                        initials = uiState.initials,
                        unreadNotificationsCount = uiState.unreadNotificationsCount,
                        onProfileClick = { navController.navigate(AppRoutes.EmployeeProfile) },
                        onNotificationClick = { navController.navigate(AppRoutes.Notifications) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Stats Row - 4 compact cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CompactStatCard(
                            value = "${uiState.allTasksCount}",
                            label = "All",
                            color = PrimaryBlue,
                            onClick = { navController.navigate(AppRoutes.EmployeeTasks(filter = "all")) },
                            modifier = Modifier.weight(1f)
                        )
                        CompactStatCard(
                            value = "${uiState.activeTasksCount}",
                            label = "Active",
                            color = InfoBlue,
                            onClick = { navController.navigate(AppRoutes.EmployeeTasks(filter = "active")) },
                            modifier = Modifier.weight(1f)
                        )
                        CompactStatCard(
                            value = "${uiState.completedTasksCount}",
                            label = "Complete",
                            color = SuccessGreen,
                            onClick = { navController.navigate(AppRoutes.EmployeeTasks(filter = "completed")) },
                            modifier = Modifier.weight(1f)
                        )
                        CompactStatCard(
                            value = "${uiState.lateTasksCount}",
                            label = "Overdue",
                            color = ErrorRed,
                            onClick = { navController.navigate(AppRoutes.EmployeeTasks(filter = "overdue")) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // Next Task Card
            item {
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                    EmployeeNextTaskCard(
                        task = uiState.nextTask,
                        onViewTask = { taskId ->
                            navController.navigate(AppRoutes.EmployeeTaskDetails(taskId))
                        }
                    )
                }
            }

            // Upcoming/Today's Tasks Section
            item {
                SectionHeader(
                    title = "Upcoming Tasks",
                    actionText = "See All →",
                    onActionClick = { navController.navigate(AppRoutes.EmployeeTasks()) },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }

            if (uiState.upcomingTasks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .height(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No upcoming tasks", 
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }
            } else {
                items(uiState.upcomingTasks) { task ->
                    Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                        EmployeeHomeTaskCard(
                            task = task,
                            onClick = { navController.navigate(AppRoutes.EmployeeTaskDetails(task.id)) }
                        )
                    }
                }
            }

            // Recent Reports Section
            item {
                SectionHeader(
                    title = "Recent Reports",
                    actionText = "See All →",
                    onActionClick = { navController.navigate(AppRoutes.EmployeeReports) },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }

            if (uiState.recentReports.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .height(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No reports submitted yet", 
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }
            } else {
                items(uiState.recentReports) { report ->
                    Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                        HomeReportCard(
                            reportContext = report,
                            onViewReport = { navController.navigate(AppRoutes.TaskReport(report.task.id)) }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmployeeHomeScreenPreview() {
    FieldFlowTheme {
        EmployeeHomeScreen(navController = rememberNavController())
    }
}
