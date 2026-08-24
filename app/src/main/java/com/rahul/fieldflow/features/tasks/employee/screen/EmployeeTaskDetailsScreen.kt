package com.rahul.fieldflow.features.tasks.employee.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rahul.fieldflow.features.tasks.components.*
import com.rahul.fieldflow.features.tasks.employee.components.TaskJourneyStatus
import com.rahul.fieldflow.features.tasks.employee.viewmodel.EmployeeTaskDetailsViewModel
import com.rahul.fieldflow.ui.theme.FieldFlowTheme
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeTaskDetailsScreen(
    taskId: String,
    onBackClick: () -> Unit,
    viewModel: EmployeeTaskDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(taskId) {
        viewModel.loadTask(taskId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Details", fontWeight = FontWeight.Bold) },
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
                CircularProgressIndicator()
            }
        } else if (uiState.task != null) {
            val task = uiState.task!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TaskStatusBadge(task.status)
                    TaskPriorityBadge(task.priority)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = task.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        TaskLocationCard(task.location)
                        TaskScheduleCard(
                            date = task.scheduledDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                            time = task.scheduledDate.format(DateTimeFormatter.ofPattern("HH:mm"))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                InstructionsCard(instructions = "Please ensure you have all the necessary tools before leaving. Call the supervisor if you encounter any unexpected issues.")

                Spacer(modifier = Modifier.height(24.dp))

                if (task.checklist.isNotEmpty()) {
                    TaskChecklistCard(task.checklist)
                    Spacer(modifier = Modifier.height(24.dp))
                }

                TaskJourneyStatus(currentStatus = task.status)
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Employee Actions
                if (task.status != com.rahul.fieldflow.features.tasks.model.TaskStatus.COMPLETED) {
                    Button(
                        onClick = { /* Handle status update */ },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "Update Status", fontWeight = FontWeight.Bold)
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun InstructionsCard(instructions: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Instructions",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = instructions, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmployeeTaskDetailsScreenPreview() {
    FieldFlowTheme {
        EmployeeTaskDetailsScreen(taskId = "1", onBackClick = {})
    }
}
