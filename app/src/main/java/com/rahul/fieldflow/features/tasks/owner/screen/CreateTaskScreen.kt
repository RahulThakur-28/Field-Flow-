package com.rahul.fieldflow.features.tasks.owner.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rahul.fieldflow.features.tasks.owner.components.TaskForm
import com.rahul.fieldflow.features.tasks.owner.viewmodel.CreateTaskViewModel
import com.rahul.fieldflow.ui.theme.FieldFlowTheme
import com.rahul.fieldflow.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    onBackClick: () -> Unit,
    onTaskCreated: () -> Unit,
    viewModel: CreateTaskViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create New Task", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Button(
                    onClick = { viewModel.createTask(onTaskCreated) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    enabled = !uiState.isSaving
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Create Task", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { padding ->
        TaskForm(
            title = uiState.title,
            onTitleChange = viewModel::updateTitle,
            description = uiState.description,
            onDescriptionChange = viewModel::updateDescription,
            location = uiState.location,
            onLocationChange = viewModel::updateLocation,
            selectedEmployee = uiState.selectedEmployee,
            onEmployeeSelected = viewModel::updateEmployee,
            priority = uiState.priority,
            onPriorityChange = viewModel::updatePriority,
            date = uiState.date,
            onDateChange = viewModel::updateDate,
            time = uiState.time,
            onTimeChange = viewModel::updateTime,
            modifier = Modifier.padding(padding)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CreateTaskScreenPreview() {
    FieldFlowTheme {
        CreateTaskScreen(onBackClick = {}, onTaskCreated = {})
    }
}
