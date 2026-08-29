package com.rahul.fieldflow.features.tasks.employee.screen

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.rahul.fieldflow.core.navigation.AppRoutes
import com.rahul.fieldflow.features.tasks.components.*
import com.rahul.fieldflow.features.tasks.employee.components.RecordingStatusIndicator
import com.rahul.fieldflow.features.tasks.employee.components.TaskJourneyStatus
import com.rahul.fieldflow.features.tasks.employee.viewmodel.EmployeeTaskDetailsViewModel
import com.rahul.fieldflow.ui.theme.*
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeTaskDetailsScreen(
    taskId: String,
    onBackClick: () -> Unit,
    onViewReportClick: (String) -> Unit,
    viewModel: EmployeeTaskDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationGranted = permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                             permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)
        val audioGranted = permissions.getOrDefault(Manifest.permission.RECORD_AUDIO, false)
        
        if (locationGranted && audioGranted) {
            viewModel.startTask()
        }
    }

    LaunchedEffect(taskId) {
        viewModel.loadTask(taskId)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Task Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    Surface(
                        onClick = onBackClick,
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.padding(8.dp).size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                                contentDescription = "Back",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else if (uiState.error != null && uiState.task == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ErrorOutline, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = uiState.error!!, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = { viewModel.loadTask(taskId) }) {
                        Text("Retry")
                    }
                }
            }
        } else if (uiState.task != null) {
            val task = uiState.task!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // 1. Task Header Card
                TaskHeaderCard(task)

                // 2. Geofence & Location
                GeofenceStatusCard(
                    locationName = task.location,
                    radius = task.radiusMeters,
                    isInside = uiState.isInsideGeofence,
                    distance = uiState.distanceToDestination
                )

                // 3. Schedule Card
                TaskScheduleCardDetailed(task)

                // 4. Instructions Card
                InstructionsCard(instructions = task.description)

                // 5. Checklist
                if (task.checklist.isNotEmpty()) {
                    TaskChecklistCard(
                        items = task.checklist,
                        onItemToggle = viewModel::toggleChecklistItem
                    )
                }

                // 6. Journey Status
                TaskJourneyStatus(currentStatus = task.status)
                
                // 7. Recording Status
                RecordingStatusIndicator(
                    state = uiState.recordingState,
                    onRetry = { viewModel.startRecordingManual() }
                )
                
                // 8. Bottom Actions
                if (task.status == com.rahul.fieldflow.features.tasks.model.TaskStatus.COMPLETED) {
                    Button(
                        onClick = { onViewReportClick(task.id) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SecondaryIndigo)
                    ) {
                        Icon(Icons.Default.Assessment, contentDescription = null)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "View Field Report", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                } else {
                    if (task.status == com.rahul.fieldflow.features.tasks.model.TaskStatus.IN_PROGRESS) {
                        Button(
                            onClick = { viewModel.stopTask() },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                        ) {
                            Text(text = "Complete Field Work", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    } else {
                        Button(
                            onClick = {
                                val hasLocationPermission = ContextCompat.checkSelfPermission(
                                    context, Manifest.permission.ACCESS_FINE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED
                                val hasAudioPermission = ContextCompat.checkSelfPermission(
                                    context, Manifest.permission.RECORD_AUDIO
                                ) == PackageManager.PERMISSION_GRANTED
                                
                                if (hasLocationPermission && hasAudioPermission) {
                                    viewModel.startTask()
                                } else {
                                    val permissions = mutableListOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION,
                                        Manifest.permission.RECORD_AUDIO
                                    )
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        permissions.add(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                    permissionLauncher.launch(permissions.toTypedArray())
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            Text(text = "Start Field Work", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }

                uiState.error?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun TaskHeaderCard(task: com.rahul.fieldflow.features.tasks.model.Task) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TASK #${task.id.take(8).uppercase()}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                TaskStatusBadge(task.status)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = task.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TaskPriorityBadge(task.priority)
            }
        }
    }
}

@Composable
private fun TaskScheduleCardDetailed(task: com.rahul.fieldflow.features.tasks.model.Task) {
    val isOverdue = task.status == com.rahul.fieldflow.features.tasks.model.TaskStatus.OVERDUE
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Event, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Scheduled Date", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        task.scheduledDate.format(DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy")),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule, 
                        contentDescription = null, 
                        tint = if (isOverdue) Color.Red else MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Deadline", 
                            style = MaterialTheme.typography.labelSmall, 
                            color = Color.Red,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            (task.deadline ?: task.scheduledDate).format(DateTimeFormatter.ofPattern("hh:mm a")),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isOverdue) Color.Red else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                
                if (isOverdue) {
                    Surface(
                        color = Color.Red.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "OVERDUE",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            color = Color.Red,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GeofenceStatusCard(
    locationName: String,
    radius: Int,
    isInside: Boolean,
    distance: Float?
) {
    val statusColor = if (isInside) Color(0xFF2E7D32) else Color(0xFFEF6C00)
    val bgColor = statusColor.copy(alpha = 0.08f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = statusColor.copy(alpha = 0.15f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isInside) Icons.Default.GpsFixed else Icons.Default.LocationOff,
                        contentDescription = null,
                        tint = statusColor
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = locationName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
                Text(
                    text = if (isInside) "Inside Geofence (${radius}m)" else "Outside Geofence (${radius}m)",
                    style = MaterialTheme.typography.bodySmall,
                    color = statusColor.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Bold
                )
                distance?.let {
                    Text(
                        text = "${it.roundToInt()}m away from site",
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun InstructionsCard(instructions: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Instructions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.1f)),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f))
        ) {
            Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.Top) {
                Icon(
                    Icons.Default.Info, 
                    contentDescription = null, 
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = instructions, 
                    style = MaterialTheme.typography.bodyLarge, 
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 24.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmployeeTaskDetailsScreenPreview() {
    FieldFlowTheme {
        EmployeeTaskDetailsScreen(taskId = "1", onBackClick = {}, onViewReportClick = {})
    }
}
