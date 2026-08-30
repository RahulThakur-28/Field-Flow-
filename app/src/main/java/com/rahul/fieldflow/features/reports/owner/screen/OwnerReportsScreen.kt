package com.rahul.fieldflow.features.reports.owner.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.rahul.fieldflow.core.navigation.AppRoutes
import com.rahul.fieldflow.features.bottomnavigation.components.FieldFlowBottomNavigation
import com.rahul.fieldflow.features.bottomnavigation.navigation.BottomNavigationConfig
import com.rahul.fieldflow.features.reports.components.ReportCard
import com.rahul.fieldflow.features.reports.owner.viewmodel.OwnerReportsViewModel
import com.rahul.fieldflow.features.tasks.components.TaskFilterTabs
import com.rahul.fieldflow.features.tasks.components.TaskSearchBar
import com.rahul.fieldflow.ui.theme.FieldFlowTheme
import com.rahul.fieldflow.ui.theme.PrimaryBlue
import com.rahul.fieldflow.ui.theme.TextSecondary

import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import com.rahul.fieldflow.features.reports.owner.components.ReportFilterTabs
import com.rahul.fieldflow.features.reports.owner.state.ReportFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerReportsScreen(
    navController: NavController,
    onReportClick: (String) -> Unit,
    viewModel: OwnerReportsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Reports", 
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "${uiState.reports.size} reports • ${uiState.needsReviewCount} need review",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    PendingBadge(count = uiState.needsReviewCount)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        bottomBar = {
            FieldFlowBottomNavigation(
                items = BottomNavigationConfig.ownerItems,
                navController = navController
            )
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = viewModel::onRefresh,
            modifier = Modifier.padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                TaskSearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = viewModel::onSearchQueryChange
                )

                Spacer(modifier = Modifier.height(16.dp))

                ReportFilterTabs(
                    selectedFilter = uiState.selectedFilter,
                    onFilterSelected = viewModel::onFilterSelected,
                    allCount = uiState.reports.size,
                    needsReviewCount = uiState.needsReviewCount,
                    reviewedCount = uiState.reviewedCount
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (uiState.reports.isEmpty() && !uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "No reports found", style = MaterialTheme.typography.bodyLarge)
                    }
                } else if (uiState.error != null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = uiState.error!!, color = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { viewModel.loadReports() }) {
                                Text("Retry")
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.filteredReports, key = { it.reportId }) { report ->
                            ReportCard(
                                report = report,
                                onClick = { onReportClick(report.id) },
                                onReviewClick = { 
                                    viewModel.markAsReviewed(report.reportId) {
                                        // Success callback handled by ViewModel refresh
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PendingBadge(count: Int) {
    if (count > 0) {
        val redColor = com.rahul.fieldflow.ui.theme.ErrorRed
        Surface(
            color = redColor.copy(alpha = 0.15f),
            shape = CircleShape,
            modifier = Modifier.padding(end = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(redColor, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$count Pending",
                    color = if (MaterialTheme.colorScheme.surface == Color(0xFF161C2C)) redColor.copy(alpha = 0.9f) else redColor,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OwnerReportsScreenPreview() {
    FieldFlowTheme {
        OwnerReportsScreen(
            navController = rememberNavController(),
            onReportClick = {}
        )
    }
}
