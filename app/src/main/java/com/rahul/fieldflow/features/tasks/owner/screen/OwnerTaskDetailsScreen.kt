package com.rahul.fieldflow.features.tasks.owner.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rahul.fieldflow.features.profile.components.ProfileAvatar
import com.rahul.fieldflow.features.tasks.components.*
import com.rahul.fieldflow.features.tasks.model.Task
import com.rahul.fieldflow.features.tasks.owner.viewmodel.OwnerTaskDetailsViewModel
import com.rahul.fieldflow.ui.theme.*
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerTaskDetailsScreen(
    taskId: String,
    onBackClick: () -> Unit,
    onTrackClick: (String) -> Unit,
    onViewReportClick: (String) -> Unit,
    viewModel: OwnerTaskDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(taskId) {
        viewModel.loadTask(taskId)
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FB),
        topBar = {
            TopAppBar(
                title = { Text("Task Details", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* More actions if needed */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            if (uiState.task != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .navigationBarsPadding(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { onTrackClick(taskId) },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                        ) {
                            Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Live Track", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        }
                        
                        OutlinedButton(
                            onClick = { onViewReportClick(taskId) },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue)
                        ) {
                            Icon(Icons.Default.Assessment, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("View Report", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else if (uiState.task == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ErrorOutline, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Task not found", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = onBackClick) {
                        Text("Go Back")
                    }
                }
            }
        } else {
            val task = uiState.task!!
            Log.d("OWNER_CHECKLIST_TRACE", "uiState checklist count=${task.checklist.size}")
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Task Overview Card
                TaskOverviewCard(task)

                // 2. Location Card
                TaskLocationCard(
                    location = task.location,
                    latitude = task.latitude,
                    longitude = task.longitude,
                    radiusMeters = task.radiusMeters
                )

                // 3. Schedule Card
                TaskScheduleCard(
                    scheduledDate = task.scheduledDate.format(DateTimeFormatter.ofPattern("EEEE, MMM dd")),
                    deadline = (task.deadline ?: task.scheduledDate).format(DateTimeFormatter.ofPattern("hh:mm a"))
                )

                // 4. Checklist Card (only if real data exists)
                Log.d("OWNER_CHECKLIST_TRACE", "render checklist count=${task.checklist.size}")
                if (task.checklist.isNotEmpty()) {
                    Log.d("OWNER_CHECKLIST_TRACE", "rendering checklist card = true")
                    TaskChecklistCard(task.checklist)
                } else {
                    Log.d("OWNER_CHECKLIST_TRACE", "rendering checklist card = false")
                }

                // 5. Timeline Card (only if real data exists)
                if (task.timeline.isNotEmpty()) {
                    TaskProgressTimeline(task.timeline)
                }

                Spacer(modifier = Modifier.height(80.dp)) // Extra padding for bottom bar
            }
        }
    }
}

@Composable
private fun TaskOverviewCard(task: Task) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // ID and Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#${task.id.takeLast(4).uppercase()} · ${task.scheduledDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Title
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = TextDark
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            Text(
                text = task.description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Badges
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TaskStatusBadge(task.status)
                TaskPriorityBadge(task.priority)
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFF0F2F5))
            Spacer(modifier = Modifier.height(16.dp))

            // Assigned Employee
            Row(verticalAlignment = Alignment.CenterVertically) {
                ProfileAvatar(
                    initials = task.assignedTo.name.take(1) + (task.assignedTo.name.split(" ").getOrNull(1)?.take(1) ?: ""),
                    modifier = Modifier.size(44.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = task.assignedTo.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        text = "Assigned Employee",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OwnerTaskDetailsScreenPreview() {
    FieldFlowTheme {
        OwnerTaskDetailsScreen(taskId = "1", onBackClick = {}, onTrackClick = {}, onViewReportClick = {})
    }
}
