package com.rahul.fieldflow.features.home.owner.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.rahul.fieldflow.core.navigation.AppRoutes
import com.rahul.fieldflow.features.bottomnavigation.components.FieldFlowBottomNavigation
import com.rahul.fieldflow.features.bottomnavigation.navigation.BottomNavigationConfig
import com.rahul.fieldflow.features.home.components.*
import com.rahul.fieldflow.features.home.owner.components.*
import com.rahul.fieldflow.features.home.owner.state.OwnerHomeUiState
import com.rahul.fieldflow.features.home.owner.viewmodel.OwnerHomeViewModel
import com.rahul.fieldflow.ui.theme.BackgroundLight
import com.rahul.fieldflow.ui.theme.FieldFlowTheme
import com.rahul.fieldflow.ui.theme.PrimaryBlue
import com.rahul.fieldflow.ui.theme.SecondaryIndigo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerHomeScreen(
    navController: NavController,
    viewModel: OwnerHomeViewModel = viewModel()
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
    uiState: OwnerHomeUiState,
    navController: NavController
) {
    Scaffold(
        containerColor = BackgroundLight,
        bottomBar = {
            FieldFlowBottomNavigation(
                items = BottomNavigationConfig.ownerItems,
                navController = navController
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(AppRoutes.Tasks) },
                containerColor = PrimaryBlue,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Task")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    PrimaryBlue.copy(alpha = 0.1f),
                                    BackgroundLight
                                )
                            )
                        )
                        .padding(horizontal = 20.dp)
                ) {
                    FieldFlowHeader(
                        date = "Friday, Aug 22 • ${uiState.location}",
                        userName = uiState.userName,
                        subtitle = "Here's what's happening today.",
                        initials = uiState.initials,
                        notificationCount = uiState.notificationCount,
                        onProfileClick = { navController.navigate(AppRoutes.OwnerProfile) },
                        onNotificationClick = { /* Handle notifications */ }
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        uiState.stats.forEach { stat ->
                            SummaryStatCard(
                                stat = stat, 
                                modifier = Modifier.weight(1f),
                                onClick = { navController.navigate(AppRoutes.Analytics) }
                            )
                        }
                    }

                    WorkflowIndicator(currentStep = uiState.currentStep)
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            item {
                SectionHeader(
                    title = "🔴 Live Field Visits",
                    actionText = "All Tasks",
                    modifier = Modifier.padding(horizontal = 20.dp),
                    onActionClick = { navController.navigate(AppRoutes.Tasks) }
                )
            }

            items(uiState.liveVisits) { visit ->
                LiveVisitCard(
                    visit = visit,
                    modifier = Modifier.padding(horizontal = 20.dp),
                    onClick = { navController.navigate(AppRoutes.Tasks) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                SectionHeader(
                    title = "Quick Actions",
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    QuickActionCard(
                        icon = Icons.Default.Add,
                        title = "New Task",
                        accentColor = Color(0xFF5267E8),
                        onClick = { navController.navigate(AppRoutes.Tasks) }
                    )
                    QuickActionCard(
                        icon = Icons.Default.MyLocation,
                        title = "Track Live",
                        accentColor = SecondaryIndigo,
                        onClick = { /* Navigate to Tracking */ }
                    )
                    QuickActionCard(
                        icon = Icons.Default.Assessment,
                        title = "Reports",
                        accentColor = Color(0xFFFF9800),
                        onClick = { navController.navigate(AppRoutes.OwnerReports) }
                    )
                }
            }

            item {
                SectionHeader(
                    title = "Team Status",
                    actionText = "View Team",
                    modifier = Modifier.padding(horizontal = 20.dp),
                    onActionClick = { navController.navigate(AppRoutes.Team) }
                )
            }

            items(uiState.teamStatus) { member ->
                TeamStatusCard(
                    member = member,
                    modifier = Modifier.padding(horizontal = 20.dp),
                    onClick = { navController.navigate(AppRoutes.Team) }
                )
            }

            item {
                SectionHeader(
                    title = "Today's Activity",
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                ActivityTimeline(
                    activities = uiState.recentActivity,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OwnerHomeScreenPreview() {
    FieldFlowTheme {
        OwnerHomeContent(
            uiState = OwnerHomeUiState(
                userName = "Rahul",
                location = "Mumbai",
                initials = "RT",
                notificationCount = 2,
                stats = emptyList(),
                liveVisits = emptyList(),
                teamStatus = emptyList(),
                recentActivity = emptyList()
            ),
            navController = rememberNavController()
        )
    }
}
