package com.rahul.fieldflow.features.team.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rahul.fieldflow.features.home.components.ProfileAvatar
import com.rahul.fieldflow.features.tasks.components.TaskCard
import com.rahul.fieldflow.features.tasks.components.TaskProgressTimeline
import com.rahul.fieldflow.features.team.components.EmployeeActivityItem
import com.rahul.fieldflow.features.team.components.EmployeePerformanceCard
import com.rahul.fieldflow.features.team.components.StatusIndicator
import com.rahul.fieldflow.features.team.viewmodel.TeamViewModel
import com.rahul.fieldflow.ui.theme.FieldFlowTheme
import com.rahul.fieldflow.ui.theme.PrimaryBlue
import com.rahul.fieldflow.ui.theme.TextDark
import com.rahul.fieldflow.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeDetailsScreen(
    employeeId: String,
    onBackClick: () -> Unit,
    onTaskClick: (String) -> Unit,
    viewModel: TeamViewModel = viewModel()
) {
    val uiState by viewModel.employeeDetailsUiState.collectAsState()

    LaunchedEffect(employeeId) {
        viewModel.loadEmployeeDetails(employeeId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Employee Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else if (uiState.member != null) {
            val member = uiState.member!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Profile Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ProfileAvatar(
                                initials = member.employee.id.take(2),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = member.employee.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                                Text(
                                    text = member.employee.role,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                StatusIndicator(status = member.status)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(16.dp))

                        ContactItem(icon = Icons.Default.Email, value = "rahul@fieldflow.in")
                        Spacer(modifier = Modifier.height(8.dp))
                        ContactItem(icon = Icons.Default.Phone, value = "+91 98765 43210")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("Performance", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                EmployeePerformanceCard(performance = member.performance)

                Spacer(modifier = Modifier.height(24.dp))

                // Current Task
                uiState.currentTask?.let { task ->
                    Text("CURRENT TASK", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                    Spacer(modifier = Modifier.height(12.dp))
                    TaskCard(task = task, onClick = { onTaskClick(task.id) })
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(
                        onClick = { onTaskClick(task.id) },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("View Task Details →", color = PrimaryBlue)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("Recent Activity", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                Column {
                    uiState.recentActivity.forEachIndexed { index, activity ->
                        EmployeeActivityItem(
                            activity = activity,
                            isLast = index == uiState.recentActivity.size - 1
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Assigned Tasks", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    TextButton(onClick = { /* View All */ }) {
                        Text("View All", color = PrimaryBlue)
                    }
                }
                
                uiState.assignedTasks.forEach { task ->
                    TaskCard(task = task, onClick = { onTaskClick(task.id) })
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun ContactItem(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = TextSecondary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = value, style = MaterialTheme.typography.bodyMedium, color = TextDark)
    }
}

@Preview(showBackground = true)
@Composable
fun EmployeeDetailsScreenPreview() {
    FieldFlowTheme {
        EmployeeDetailsScreen(employeeId = "1", onBackClick = {}, onTaskClick = {})
    }
}
