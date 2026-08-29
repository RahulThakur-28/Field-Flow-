package com.rahul.fieldflow.features.team.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GroupAdd
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
        containerColor = Color(0xFFF8F9FB),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Your Team", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextDark)
                        Text(
                            "${uiState.teamMemberCount} total members",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                },
                actions = {
                    Surface(
                        onClick = onNavigateToRequests,
                        shape = RoundedCornerShape(12.dp),
                        color = PrimaryBlue.copy(alpha = 0.08f),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.GroupAdd, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Requests", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF8F9FB))
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
        ) {
            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                TaskSearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = viewModel::onSearchQueryChange
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlue, strokeWidth = 3.dp)
                }
            } else if (uiState.filteredMembers.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No team members found", color = TextSecondary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.filteredMembers) { member ->
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
