package com.rahul.fieldflow.features.team.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.rahul.fieldflow.features.home.components.SummaryStatCard
import com.rahul.fieldflow.features.tasks.components.TaskSearchBar
import com.rahul.fieldflow.features.home.model.StatusBadgeType
import com.rahul.fieldflow.features.home.model.SummaryStatUiModel
import com.rahul.fieldflow.features.team.components.TeamMemberCard
import com.rahul.fieldflow.features.team.model.mockTeamMembers
import com.rahul.fieldflow.features.team.viewmodel.TeamViewModel
import com.rahul.fieldflow.ui.theme.FieldFlowTheme
import com.rahul.fieldflow.ui.theme.PrimaryBlue

import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerTeamScreen(
    navController: NavController,
    onMemberClick: (String) -> Unit,
    onNavigateToRequests: () -> Unit,
    viewModel: TeamViewModel = hiltViewModel()
) {
    val uiState by viewModel.teamUiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Your Team", fontWeight = FontWeight.Bold)
                        Text(
                            "${uiState.teamMembers.size} members",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToRequests) {
                        Icon(Icons.Default.Add, contentDescription = "Employee Requests")
                    }
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

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryStatCard(
                    stat = SummaryStatUiModel(
                        value = "${uiState.activeCount}",
                        label = "Active Now",
                        type = StatusBadgeType.ACTIVE
                    ),
                    modifier = Modifier.weight(1f)
                )
                SummaryStatCard(
                    stat = SummaryStatUiModel(
                        value = "${uiState.avgOnTime}%",
                        label = "Avg On-Time",
                        type = StatusBadgeType.SUCCESS
                    ),
                    modifier = Modifier.weight(1f)
                )
                SummaryStatCard(
                    stat = SummaryStatUiModel(
                        value = "${uiState.totalTasks}",
                        label = "Total Tasks",
                        type = StatusBadgeType.NEUTRAL
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(uiState.filteredMembers) { member ->
                        TeamMemberCard(
                            member = member,
                            onClick = { onMemberClick(member.employee.id) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OwnerTeamScreenPreview() {
    FieldFlowTheme {
        OwnerTeamScreen(
            navController = rememberNavController(),
            onMemberClick = {},
            onNavigateToRequests = {}
        )
    }
}
