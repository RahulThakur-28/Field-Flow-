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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerReportsScreen(
    navController: NavController,
    onReportClick: (String) -> Unit,
    viewModel: OwnerReportsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Reports", fontWeight = FontWeight.Bold)
                        Text(
                            text = "${uiState.reports.size} reports • ${uiState.needsReviewCount} need review",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary
                        )
                    }
                },
                actions = {
                    PendingBadge(count = uiState.needsReviewCount)
                }
            )
        },
        bottomBar = {
            FieldFlowBottomNavigation(
                items = BottomNavigationConfig.ownerItems,
                navController = navController
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            TaskSearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::onSearchQueryChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            TaskFilterTabs(
                tabs = listOf("All", "Needs Review", "Reviewed"),
                selectedTab = uiState.selectedTab,
                onTabSelected = viewModel::onTabSelected
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(uiState.filteredReports) { report ->
                        ReportCard(
                            report = report,
                            onClick = { onReportClick(report.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PendingBadge(count: Int) {
    if (count > 0) {
        Surface(
            color = Color(0xFFFF9800).copy(alpha = 0.1f),
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
                        .background(Color(0xFFFF9800), CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$count Pending",
                    color = Color(0xFFFF9800),
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
