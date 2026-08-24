package com.rahul.fieldflow.features.tasks.employee.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.rahul.fieldflow.core.navigation.AppRoutes
import com.rahul.fieldflow.features.bottomnavigation.components.FieldFlowBottomNavigation
import com.rahul.fieldflow.features.bottomnavigation.navigation.BottomNavigationConfig
import com.rahul.fieldflow.features.tasks.components.TaskCard
import com.rahul.fieldflow.features.tasks.components.TaskFilterTabs
import com.rahul.fieldflow.features.tasks.employee.state.EmployeeTasksUiState
import com.rahul.fieldflow.features.tasks.employee.viewmodel.EmployeeTasksViewModel
import com.rahul.fieldflow.features.tasks.model.TaskStatus
import com.rahul.fieldflow.ui.theme.FieldFlowTheme
import com.rahul.fieldflow.ui.theme.PrimaryBlue
import com.rahul.fieldflow.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeTasksScreen(
    navController: NavController,
    onTaskClick: (String) -> Unit,
    viewModel: EmployeeTasksViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Tasks", fontWeight = FontWeight.Bold) }
            )
        },
        bottomBar = {
            FieldFlowBottomNavigation(
                items = BottomNavigationConfig.employeeItems,
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
            TaskFilterTabs(
                tabs = listOf("Upcoming", "Active", "Completed"),
                selectedTab = uiState.selectedTab,
                onTabSelected = viewModel::onTabSelected
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            } else {
                val filteredTasks = when (uiState.selectedTab) {
                    0 -> uiState.tasks.filter { it.status == TaskStatus.PENDING }
                    1 -> uiState.tasks.filter { it.status == TaskStatus.IN_PROGRESS }
                    2 -> uiState.tasks.filter { it.status == TaskStatus.COMPLETED }
                    else -> uiState.tasks
                }

                if (filteredTasks.isEmpty()) {
                    EmptyState(tabIndex = uiState.selectedTab)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(filteredTasks) { task ->
                            TaskCard(
                                task = task,
                                onClick = { onTaskClick(task.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(tabIndex: Int) {
    val message = when (tabIndex) {
        0 -> "No upcoming tasks"
        1 -> "No active tasks"
        2 -> "No completed tasks"
        else -> "You're all caught up!"
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "✨",
            fontSize = 64.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "You're all caught up!",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmployeeTasksScreenPreview() {
    FieldFlowTheme {
        EmployeeTasksScreen(
            navController = rememberNavController(),
            onTaskClick = {}
        )
    }
}
