package com.rahul.fieldflow.features.reports.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                },
                actions = {
                    IconButton(onClick = { viewModel.loadReport(taskId) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = { viewModel.loadReport(taskId) },
            modifier = Modifier.padding(padding)
        ) {
            if (uiState.isLoading && uiState.reportContext == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            } else if (uiState.error != null && uiState.reportContext == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
                        Button(onClick = { viewModel.loadReport(taskId) }) {
                            Text("Retry")
                        }
                    }
                }
            } else if (uiState.reportContext != null) {
                val context = uiState.reportContext!!
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentPadding = PaddingValues(bottom = 40.dp)
                ) {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 2.dp
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                ReportOverview(context)
                            }
                        }
                    }

                    if (context.aiReport != null) {
                        item {
                            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)) {
                                AiReportSection(context.aiReport)
                            }
                        }
                    } else {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(32.dp),
                                        strokeWidth = 3.dp,
                                        color = PrimaryBlue
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "AI is analyzing the report...", 
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                            Text(
                                text = "Recordings", 
                                style = MaterialTheme.typography.titleLarge, 
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    items(context.sessions.size) { index ->
                        Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                            val session = context.sessions[index]
                            RecordingSessionItem(
                                session = session,
                                onGetUrl = { viewModel.getAudioUrl(session.storagePath ?: "") }
                            )
                        }
                    }

                    if (context.transcripts.isNotEmpty()) {
                        item {
                            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                Spacer(modifier = Modifier.height(24.dp))
                                TimelineSection(context.sessions, context.transcripts)
                            }
                        }
                    }
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
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                shape = MaterialTheme.shapes.extraSmall
            ) {
                Text(
                    text = "TASK #${task.id.take(8).uppercase()}",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            TaskStatusBadge(com.rahul.fieldflow.features.tasks.model.TaskStatus.valueOf(task.status.name))
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = task.title, 
            style = MaterialTheme.typography.headlineMedium, 
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(), 
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "ASSIGNED EMPLOYEE", 
                    style = MaterialTheme.typography.labelSmall, 
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.assignedEmployee?.fullName ?: "Unassigned", 
                    style = MaterialTheme.typography.bodyLarge, 
                    fontWeight = FontWeight.SemiBold
                )
            }
            Column {
                Text(
                    text = "PRIORITY", 
                    style = MaterialTheme.typography.labelSmall, 
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                TaskPriorityBadge(com.rahul.fieldflow.features.tasks.model.TaskPriority.valueOf(task.priority.name))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column {
            Text(
                text = "COMPLETION DATE", 
                style = MaterialTheme.typography.labelSmall, 
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = task.completedAt?.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm")) ?: "Not completed",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

