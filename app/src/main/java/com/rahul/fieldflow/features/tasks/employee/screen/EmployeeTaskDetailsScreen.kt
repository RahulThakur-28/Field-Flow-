package com.rahul.fieldflow.features.tasks.employee.screen

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.rahul.fieldflow.features.tasks.components.*
import com.rahul.fieldflow.features.tasks.employee.components.TaskJourneyStatus
import com.rahul.fieldflow.features.tasks.employee.viewmodel.EmployeeTaskDetailsViewModel
import com.rahul.fieldflow.ui.theme.FieldFlowTheme
import com.rahul.fieldflow.ui.theme.PrimaryBlue
import com.rahul.fieldflow.ui.theme.TextDark
import com.rahul.fieldflow.ui.theme.TextSecondary
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeTaskDetailsScreen(
    taskId: String,
    onBackClick: () -> Unit,
    viewModel: EmployeeTaskDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                      permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)
        if (granted) {
            viewModel.startTask()
        }
    }

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
                CircularProgressIndicator(color = PrimaryBlue)
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
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Location & Geofence Status Card
                GeofenceStatusCard(
                    locationName = task.location,
                    radius = task.radiusMeters,
                    isInside = uiState.isInsideGeofence,
                    distance = uiState.distanceToDestination
                )

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
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
                
                // Action Button
                if (task.status != com.rahul.fieldflow.features.tasks.model.TaskStatus.COMPLETED) {
                    if (uiState.isTrackingActive) {
                        Button(
                            onClick = { viewModel.stopTask() },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                        ) {
                            Text(text = "Complete Field Work", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = {
                                val hasPermission = ContextCompat.checkSelfPermission(
                                    context, Manifest.permission.ACCESS_FINE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED
                                
                                if (hasPermission) {
                                    viewModel.startTask()
                                } else {
                                    permissionLauncher.launch(
                                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            Text(text = "Start Field Work", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                uiState.error?.let { error ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isInside) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = if (isInside) Color(0xFF4CAF50).copy(alpha = 0.2f) else Color(0xFFFF9800).copy(alpha = 0.2f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isInside) Icons.Default.GpsFixed else Icons.Default.LocationOff,
                        contentDescription = null,
                        tint = if (isInside) Color(0xFF2E7D32) else Color(0xFFEF6C00)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = locationName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Text(
                    text = if (isInside) "Inside Geofence (${radius}m)" else "Outside Geofence (${radius}m)",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isInside) Color(0xFF2E7D32) else Color(0xFFEF6C00),
                    fontWeight = FontWeight.Bold
                )
                distance?.let {
                    Text(
                        text = "${it.roundToInt()}m away",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
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
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                Icon(
                    Icons.Default.Info, 
                    contentDescription = null, 
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = instructions, style = MaterialTheme.typography.bodyMedium, color = TextDark)
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
