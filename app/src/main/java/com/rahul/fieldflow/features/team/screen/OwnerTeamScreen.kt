package com.rahul.fieldflow.features.team.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.rahul.fieldflow.features.tasks.components.TaskSearchBar
import com.rahul.fieldflow.features.team.components.TeamMemberCard
import com.rahul.fieldflow.features.team.viewmodel.TeamViewModel
import com.rahul.fieldflow.ui.theme.*

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
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Your Team", 
                            style = MaterialTheme.typography.titleLarge, 
                            fontWeight = FontWeight.Bold, 
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "${uiState.teamMemberCount} total members",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.5.sp
                        )
                    }
                },
                actions = {
                    Surface(
                        onClick = onNavigateToRequests,
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.GroupAdd, 
                                contentDescription = null, 
                                tint = MaterialTheme.colorScheme.primary, 
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Requests", 
                                style = MaterialTheme.typography.labelMedium, 
                                fontWeight = FontWeight.Bold, 
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
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
                items = BottomNavigationConfig.ownerItems,
                navController = navController
            )
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = viewModel::refreshTeam,
            modifier = Modifier.padding(padding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                    TaskSearchBar(
                        query = uiState.searchQuery,
                        onQueryChange = viewModel::onSearchQueryChange
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (uiState.filteredMembers.isEmpty() && !uiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 100.dp), 
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("👥", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No team members found", 
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 40.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(uiState.filteredMembers, key = { it.profile.id }) { member ->
                            TeamMemberCard(
                                member = member,
                                onClick = { onMemberClick(member.profile.id) }
                            )
                        }
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
