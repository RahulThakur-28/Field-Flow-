package com.rahul.fieldflow.features.reports.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rahul.fieldflow.features.reports.components.AiReportSection
import com.rahul.fieldflow.features.reports.components.RecordingSessionItem
import com.rahul.fieldflow.features.reports.components.TimelineSection
import com.rahul.fieldflow.features.reports.viewmodel.TaskReportViewModel
import com.rahul.fieldflow.features.tasks.components.TaskPriorityBadge
import com.rahul.fieldflow.features.tasks.components.TaskStatusBadge
import com.rahul.fieldflow.ui.theme.PrimaryBlue
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskReportScreen(
    taskId: String,
    onBackClick: () -> Unit,
    viewModel: TaskReportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(taskId) {
        viewModel.loadReport(taskId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Report", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else if (uiState.error != null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
                    Button(onClick = { viewModel.loadReport(taskId) }) {
                        Text("Retry")
                    }
                }
            }
        } else if (uiState.reportContext != null) {
            val context = uiState.reportContext!!
            LaunchedEffect(context) {
                android.util.Log.d("REPORT_DEBUG", "REPORT_UI_VISIBLE taskId=${context.task.id} aiReportPresent=${context.aiReport != null} sessions=${context.sessions.size} transcripts=${context.transcripts.size}")
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                item {
                    ReportOverview(context)
                    Divider(modifier = Modifier.padding(vertical = 16.dp))
                }

                if (context.aiReport != null) {
                    item {
                        AiReportSection(context.aiReport)
                        Divider(modifier = Modifier.padding(vertical = 16.dp))
                    }
                } else {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "AI Report is being prepared...", color = Color.Gray)
                        }
                    }
                }

                item {
                    Text(text = "Recording Sessions", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(context.sessions.size) { index ->
                    val session = context.sessions[index]
                    RecordingSessionItem(
                        session = session,
                        onGetUrl = { viewModel.getAudioUrl(session.storagePath ?: "") }
                    )
                }

                item {
                    Divider(modifier = Modifier.padding(vertical = 16.dp))
                    TimelineSection(context.sessions, context.transcripts)
                }
            }
        }
    }
}

@Composable
fun ReportOverview(context: com.rahul.fieldflow.domain.model.TaskReportContext) {
    val task = context.task
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TASK #${task.id.take(8).uppercase()}",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )
            TaskStatusBadge(com.rahul.fieldflow.features.tasks.model.TaskStatus.valueOf(task.status.name))
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(text = task.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(text = "Assigned To", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Text(text = task.assignedEmployee?.fullName ?: "Unassigned", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            }
            TaskPriorityBadge(com.rahul.fieldflow.features.tasks.model.TaskPriority.valueOf(task.priority.name))
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(text = "Completion", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Text(
            text = task.completedAt?.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")) ?: "Not completed",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun Divider(modifier: Modifier = Modifier) {
    HorizontalDivider(modifier = modifier, color = Color.LightGray.copy(alpha = 0.3f))
}
