package com.rahul.fieldflow.features.analytics.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.rahul.fieldflow.core.navigation.AppRoutes
import com.rahul.fieldflow.features.analytics.components.*
import com.rahul.fieldflow.features.analytics.state.AnalyticsUiState
import com.rahul.fieldflow.features.analytics.viewmodel.AnalyticsViewModel
import com.rahul.fieldflow.features.bottomnavigation.components.FieldFlowBottomNavigation
import com.rahul.fieldflow.features.bottomnavigation.navigation.BottomNavigationConfig
import com.rahul.fieldflow.ui.theme.BackgroundLight
import com.rahul.fieldflow.ui.theme.FieldFlowTheme
import com.rahul.fieldflow.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerAnalyticsScreen(
    navController: NavController,
    viewModel: AnalyticsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analytics", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundLight
                )
            )
        },
        bottomBar = {
            FieldFlowBottomNavigation(
                items = BottomNavigationConfig.ownerItems,
                navController = navController
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else {
            OwnerAnalyticsContent(
                uiState = uiState,
                onPeriodSelected = viewModel::onPeriodSelected,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun OwnerAnalyticsContent(
    uiState: AnalyticsUiState,
    onPeriodSelected: (com.rahul.fieldflow.features.analytics.model.AnalyticsPeriod) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            AnalyticsPeriodTabs(
                selectedPeriod = uiState.selectedPeriod,
                onPeriodSelected = onPeriodSelected
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    uiState.summaryStats.take(2).forEach { stat ->
                        AnalyticsStatCard(stat = stat, modifier = Modifier.weight(1f))
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    uiState.summaryStats.drop(2).forEach { stat ->
                        AnalyticsStatCard(stat = stat, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        item {
            TasksOverTimeCard(data = uiState.tasksOverTime)
        }

        item {
            CompletionTrendCard(data = uiState.completionTrend)
        }

        item {
            TaskBreakdownCard(data = uiState.taskBreakdown)
        }

        item {
            TopPerformersCard(performers = uiState.topPerformers)
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OwnerAnalyticsScreenPreview() {
    FieldFlowTheme {
        OwnerAnalyticsScreen(navController = rememberNavController())
    }
}
