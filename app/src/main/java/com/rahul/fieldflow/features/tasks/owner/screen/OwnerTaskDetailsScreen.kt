package com.rahul.fieldflow.features.tasks.owner.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rahul.fieldflow.features.tasks.components.*
import com.rahul.fieldflow.features.tasks.owner.viewmodel.OwnerTaskDetailsViewModel
import com.rahul.fieldflow.ui.theme.FieldFlowTheme
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerTaskDetailsScreen(
    taskId: String,
    onBackClick: () -> Unit,
    onEditClick: (String) -> Unit,
    onTrackClick: (String) -> Unit,
    viewModel: OwnerTaskDetailsViewModel = hiltViewModel()
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
                },
                actions = {
                    IconButton(onClick = { /* More actions */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
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
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        TaskLocationCard(task.location)
                        TaskScheduleCard(
                            date = task.scheduledDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                            time = task.scheduledDate.format(DateTimeFormatter.ofPattern("HH:mm"))
                        )
                        DetailItem(
                            Icons.Default.Person,
                            "Assigned To",
                            task.assignedTo.name
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (task.checklist.isNotEmpty()) {
                    TaskChecklistCard(task.checklist)
                    Spacer(modifier = Modifier.height(24.dp))
                }

                if (task.timeline.isNotEmpty()) {
                    TaskProgressTimeline(task.timeline)
                    Spacer(modifier = Modifier.height(24.dp))
                }

                TaskActionButtons(
                    onEdit = { onEditClick(task.id) },
                    onTrack = { onTrackClick(task.id) }
                )
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OwnerTaskDetailsScreenPreview() {
    FieldFlowTheme {
        OwnerTaskDetailsScreen(taskId = "1", onBackClick = {}, onEditClick = {}, onTrackClick = {})
    }
}
