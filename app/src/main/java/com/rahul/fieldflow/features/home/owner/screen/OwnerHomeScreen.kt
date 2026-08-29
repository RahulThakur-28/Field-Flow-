package com.rahul.fieldflow.features.home.owner.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.rahul.fieldflow.features.home.owner.components.*
import com.rahul.fieldflow.features.home.owner.viewmodel.OwnerHomeViewModel
import com.rahul.fieldflow.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerHomeScreen(
    navController: NavController,
    viewModel: OwnerHomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    PullToRefreshBox(
        isRefreshing = uiState.isLoading,
        onRefresh = { viewModel.refresh() }
    ) {
        OwnerHomeContent(
            uiState = uiState,
            navController = navController
        )
    }
}

@Composable
fun OwnerHomeContent(
    uiState: com.rahul.fieldflow.features.home.owner.state.OwnerHomeUiState,
    navController: NavController
) {
    Scaffold(
        containerColor = Color(0xFFF8F9FB),
        bottomBar = {
            FieldFlowBottomNavigation(
                items = BottomNavigationConfig.ownerItems,
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
                                    PrimaryBlue.copy(alpha = 0.06f),
                                    Color(0xFFF8F9FB)
                                )
                            )
                        )
                        .padding(horizontal = 20.dp)
                ) {
                    HomeGreetingHeader(
                        userName = uiState.userName,
                        initials = uiState.initials,
                        onProfileClick = { navController.navigate(AppRoutes.OwnerProfile) }
                    )

                    CompanyInfoCard(
                        companyName = uiState.companyName,
                        companyId = uiState.companyId
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Stats Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CompactStatCard(
                            value = "${uiState.totalTasksCount}",
                            label = "All",
                            color = PrimaryBlue,
                            onClick = { navController.navigate(AppRoutes.OwnerTasks(filter = "all")) },
                            modifier = Modifier.weight(1f)
                        )
                        CompactStatCard(
                            value = "${uiState.activeTasksCount}",
                            label = "Active",
                            color = Color(0xFF2196F3),
                            onClick = { navController.navigate(AppRoutes.OwnerTasks(filter = "active")) },
                            modifier = Modifier.weight(1f)
                        )
                        CompactStatCard(
                            value = "${uiState.completedTasksCount}",
                            label = "Complete",
                            color = Color(0xFF4CAF50),
                            onClick = { navController.navigate(AppRoutes.OwnerTasks(filter = "completed")) },
                            modifier = Modifier.weight(1f)
                        )
                        CompactStatCard(
                            value = "${uiState.lateTasksCount}",
                            label = "Overdue",
                            color = Color(0xFFF44336),
                            onClick = { navController.navigate(AppRoutes.OwnerTasks(filter = "overdue")) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // Latest Tasks Section
            item {
                SectionHeader(
                    title = "New Tasks",
                    actionText = "See All →",
                    onActionClick = { navController.navigate(AppRoutes.OwnerTasks()) },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }
            
            if (uiState.latestTasks.isEmpty()) {
                item {
                    EmptyStatePlaceholder("No new tasks", modifier = Modifier.padding(horizontal = 20.dp))
                }
            } else {
                items(uiState.latestTasks) { task ->
                    Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                        HomeTaskCard(
                            task = task,
                            onClick = { navController.navigate(AppRoutes.TaskDetails(task.id)) }
                        )
                    }
                }
            }

            // Latest Reports Section
            item {
                SectionHeader(
                    title = "New Reports",
                    actionText = "See All →",
                    onActionClick = { navController.navigate(AppRoutes.OwnerReports) },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }
            
            if (uiState.latestReports.isEmpty()) {
                item {
                    EmptyStatePlaceholder("No new reports", modifier = Modifier.padding(horizontal = 20.dp))
                }
            } else {
                items(uiState.latestReports) { report ->
                    Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                        HomeReportCard(
                            reportContext = report,
                            onViewReport = { navController.navigate(AppRoutes.TaskReport(report.task.id)) }
                        )
                    }
                }
            }

            // Team Preview Section
            item {
                SectionHeader(
                    title = "Team",
                    actionText = "View Team →",
                    onActionClick = { navController.navigate(AppRoutes.Team) },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }
            
            if (uiState.teamPreview.isEmpty()) {
                item {
                    EmptyStatePlaceholder("No team members found", modifier = Modifier.padding(horizontal = 20.dp))
                }
            } else {
                items(uiState.teamPreview) { employee ->
                    Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 2.dp)) {
                        HomeEmployeePreviewCard(
                            member = employee,
                            onClick = { navController.navigate(AppRoutes.Team) }
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

@Composable
fun EmptyStatePlaceholder(text: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0F2F5))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = text, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OwnerHomeScreenPreview() {
    FieldFlowTheme {
        OwnerHomeScreen(navController = rememberNavController())
    }
}
