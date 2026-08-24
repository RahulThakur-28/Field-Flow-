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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.rahul.fieldflow.core.navigation.AppRoutes
import com.rahul.fieldflow.domain.model.JoinRequest
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
    viewModel: OwnerHomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    PullToRefreshBox(
        isRefreshing = uiState.isLoading,
        onRefresh = { viewModel.refresh() }
    ) {
        OwnerHomeContent(
            uiState = uiState,
            navController = navController,
            onApprove = viewModel::approveRequest,
            onReject = viewModel::rejectRequest
        )
    }
}

@Composable
fun OwnerHomeContent(
    uiState: OwnerHomeUiState,
    navController: NavController,
    onApprove: (String) -> Unit = {},
    onReject: (String) -> Unit = {}
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

                    uiState.companyId?.let { code ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = PrimaryBlue.copy(alpha = 0.05f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Business, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Company ID:", style = MaterialTheme.typography.labelMedium, color = PrimaryBlue)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = code, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryBlue, letterSpacing = 2.sp)
                            }
                        }
                    }

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

            if (uiState.pendingRequests.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Pending Join Requests",
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
                items(uiState.pendingRequests) { request ->
                    JoinRequestCard(
                        request = request,
                        onApprove = { onApprove(request.id) },
                        onReject = { onReject(request.id) },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                    )
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

@Composable
fun JoinRequestCard(
    request: JoinRequest,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = PrimaryBlue.copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = request.employeeName?.take(1)?.uppercase() ?: "?",
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = request.employeeName ?: "Unknown Employee",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1C1E)
                    )
                    Text(
                        text = "Requested to join",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF74777F)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red.copy(alpha = 0.5f))
                ) {
                    Text("Reject")
                }
                Button(
                    onClick = onApprove,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Approve")
                }
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
