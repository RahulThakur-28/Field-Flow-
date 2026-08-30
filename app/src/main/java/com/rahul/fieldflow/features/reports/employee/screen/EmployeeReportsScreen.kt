package com.rahul.fieldflow.features.reports.employee.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.rahul.fieldflow.core.navigation.AppRoutes
import com.rahul.fieldflow.features.bottomnavigation.components.FieldFlowBottomNavigation
import com.rahul.fieldflow.features.bottomnavigation.navigation.BottomNavigationConfig
import com.rahul.fieldflow.features.reports.employee.components.EmployeeReportCard
import com.rahul.fieldflow.features.reports.employee.viewmodel.EmployeeReportsViewModel
import com.rahul.fieldflow.features.tasks.components.TaskSearchBar
import com.rahul.fieldflow.features.reports.owner.components.ReportFilterTabs
import com.rahul.fieldflow.ui.theme.FieldFlowTheme
import com.rahul.fieldflow.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeReportsScreen(
    navController: NavController,
    onReportClick: (String) -> Unit,
    viewModel: EmployeeReportsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(
                            text = "My Reports", 
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        if (!uiState.isLoading && uiState.reports.isNotEmpty()) {
                            Text(
                                text = "${uiState.reports.size} submissions",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                },
                navigationIcon = {
                    Surface(
                        onClick = { navController.popBackStack() },
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.padding(8.dp).size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                                contentDescription = "Back",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::loadReports) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
            FieldFlowBottomNavigation(
                items = BottomNavigationConfig.employeeItems,
                navController = navController
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            TaskSearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::onSearchQueryChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            ReportFilterTabs(
                selectedFilter = when(uiState.selectedTab) {
                    1 -> com.rahul.fieldflow.features.reports.owner.state.ReportFilter.NEEDS_REVIEW
                    2 -> com.rahul.fieldflow.features.reports.owner.state.ReportFilter.REVIEWED
                    else -> com.rahul.fieldflow.features.reports.owner.state.ReportFilter.ALL
                },
                onFilterSelected = { filter ->
                    val index = when(filter) {
                        com.rahul.fieldflow.features.reports.owner.state.ReportFilter.ALL -> 0
                        com.rahul.fieldflow.features.reports.owner.state.ReportFilter.NEEDS_REVIEW -> 1
                        com.rahul.fieldflow.features.reports.owner.state.ReportFilter.REVIEWED -> 2
                    }
                    viewModel.onTabSelected(index)
                },
                allCount = uiState.reports.size,
                needsReviewCount = uiState.reports.count { it.status == com.rahul.fieldflow.features.reports.model.ReportStatus.NEEDS_REVIEW },
                reviewedCount = uiState.reports.count { it.status == com.rahul.fieldflow.features.reports.model.ReportStatus.REVIEWED }
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (uiState.isLoading && uiState.reports.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            } else if (uiState.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = uiState.error!!, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { viewModel.loadReports() }) {
                            Text("Retry")
                        }
                    }
                }
            } else if (uiState.filteredReports.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No reports found", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.filteredReports, key = { it.reportId }) { report ->
                        EmployeeReportCard(
                            report = report,
                            onClick = { onReportClick(report.id) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmployeeReportsScreenPreview() {
    FieldFlowTheme {
        EmployeeReportsScreen(
            navController = rememberNavController(),
            onReportClick = {}
        )
    }
}
