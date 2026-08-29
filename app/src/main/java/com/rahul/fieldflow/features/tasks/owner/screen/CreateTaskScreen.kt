package com.rahul.fieldflow.features.tasks.owner.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rahul.fieldflow.features.tasks.model.SelectedLocation
import com.rahul.fieldflow.features.tasks.owner.components.TaskForm
import com.rahul.fieldflow.features.tasks.owner.viewmodel.CreateTaskViewModel
import com.rahul.fieldflow.ui.theme.FieldFlowTheme
import com.rahul.fieldflow.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    onBackClick: () -> Unit,
    onTaskCreated: () -> Unit,
    onPickOnMap: (Double?, Double?, Int) -> Unit,
    selectedLocation: SelectedLocation? = null,
    viewModel: CreateTaskViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(selectedLocation) {
        selectedLocation?.let {
            viewModel.onLocationSelected(it)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Create New Task", fontWeight = FontWeight.Bold) },
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
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 16.dp,
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            ) {
                Button(
                    onClick = { viewModel.createTask(onTaskCreated) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    enabled = !uiState.isSaving,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Create Task", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    ) { padding ->
        TaskForm(
            title = uiState.title,
            onTitleChange = viewModel::updateTitle,
            titleError = uiState.titleError,
            description = uiState.description,
            onDescriptionChange = viewModel::updateDescription,
            descriptionError = uiState.descriptionError,
            location = uiState.location,
            onLocationChange = viewModel::updateLocation,
            locationError = uiState.locationError,
            selectedEmployee = uiState.selectedEmployee,
            employees = uiState.employees,
            onEmployeeSelected = viewModel::updateEmployee,
            employeeError = uiState.employeeError,
            priority = uiState.priority,
            onPriorityChange = viewModel::updatePriority,
            date = uiState.date,
            onDateChange = viewModel::updateDate,
            dateError = uiState.dateError,
            startTime = uiState.startTime,
            onStartTimeChange = viewModel::updateStartTime,
            startTimeError = uiState.startTimeError,
            deadline = uiState.deadline,
            onDeadlineChange = viewModel::updateDeadline,
            deadlineError = uiState.deadlineError,
            instructions = uiState.instructions,
            onInstructionsChange = viewModel::updateInstructions,
            checklist = uiState.checklist,
            onAddChecklistItem = viewModel::addChecklistItem,
            onRemoveChecklistItem = viewModel::removeChecklistItem,
            onPickOnMap = {
                onPickOnMap(uiState.latitude, uiState.longitude, uiState.radiusMeters)
            },
            generalError = uiState.generalError,
            modifier = Modifier.padding(padding)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CreateTaskScreenPreview() {
    FieldFlowTheme {
        CreateTaskScreen(onBackClick = {}, onTaskCreated = {}, onPickOnMap = { _, _, _ -> })
    }
}
