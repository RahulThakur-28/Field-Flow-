package com.rahul.fieldflow.features.tasks.owner.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.rahul.fieldflow.features.bottomnavigation.components.FieldFlowBottomNavigation
import com.rahul.fieldflow.features.bottomnavigation.navigation.BottomNavigationConfig
import com.rahul.fieldflow.features.tasks.components.PremiumTaskCard
import com.rahul.fieldflow.features.tasks.components.TaskSearchBar
import com.rahul.fieldflow.features.tasks.owner.components.OwnerTaskFilter
import com.rahul.fieldflow.features.tasks.owner.viewmodel.OwnerTasksViewModel
import com.rahul.fieldflow.ui.theme.BackgroundLight
import com.rahul.fieldflow.ui.theme.FieldFlowTheme
import com.rahul.fieldflow.ui.theme.PrimaryBlue
import com.rahul.fieldflow.ui.theme.TextDark
import com.rahul.fieldflow.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerTasksScreen(
    navController: NavController,
    onTaskClick: (String) -> Unit,
    onCreateTaskClick: () -> Unit,
    viewModel: OwnerTasksViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Tasks", 
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateTaskClick,
                containerColor = PrimaryBlue,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Task",
                    modifier = Modifier.size(24.dp)
                )
            }
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
            Spacer(modifier = Modifier.height(8.dp))
            
            TaskSearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::onSearchQueryChange
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OwnerTaskFilter(
                selectedFilter = uiState.selectedFilter,
                onFilterSelected = viewModel::onFilterSelected
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            } else if (uiState.filteredTasks.isEmpty()) {
                EmptyTasksState(
                    isSearch = uiState.searchQuery.isNotBlank()
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.filteredTasks, key = { it.id }) { task ->
                        PremiumTaskCard(
                            task = task,
                            onClick = { onTaskClick(task.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyTasksState(isSearch: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isSearch) "No matching tasks" else "No tasks found",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextDark
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (isSearch) "Try adjusting your search query or filters" else "Create your first task to get started",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OwnerTasksScreenPreview() {
    FieldFlowTheme {
        OwnerTasksScreen(
            navController = rememberNavController(),
            onTaskClick = {},
            onCreateTaskClick = {}
        )
    }
}
