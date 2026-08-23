package com.rahul.fieldflow.features.tasks.owner.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rahul.fieldflow.features.tasks.model.Employee
import com.rahul.fieldflow.features.tasks.model.TaskPriority
import com.rahul.fieldflow.features.tasks.model.mockEmployees
import com.rahul.fieldflow.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskForm(
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    location: String,
    onLocationChange: (String) -> Unit,
    selectedEmployee: Employee?,
    onEmployeeSelected: (Employee) -> Unit,
    priority: TaskPriority,
    onPriorityChange: (TaskPriority) -> Unit,
    date: String,
    onDateChange: (String) -> Unit,
    time: String,
    onTimeChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Basic Information",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text("Task Title") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )

        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            shape = RoundedCornerShape(8.dp)
        )

        Divider()

        Text(
            text = "Assignment & Priority",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        EmployeeSelector(
            selectedEmployee = selectedEmployee,
            onEmployeeSelected = onEmployeeSelected
        )

        Text("Priority", style = MaterialTheme.typography.labelMedium)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TaskPriority.values().forEach { p ->
                FilterChip(
                    selected = priority == p,
                    onClick = { onPriorityChange(p) },
                    label = { Text(p.label) },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryBlue,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Divider()

        Text(
            text = "Schedule & Location",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = location,
            onValueChange = onLocationChange,
            label = { Text("Location") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
            shape = RoundedCornerShape(8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = date,
                onValueChange = onDateChange,
                label = { Text("Date") },
                modifier = Modifier.weight(1f),
                leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                shape = RoundedCornerShape(8.dp)
            )
            OutlinedTextField(
                value = time,
                onValueChange = onTimeChange,
                label = { Text("Time") },
                modifier = Modifier.weight(1f),
                leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null) },
                shape = RoundedCornerShape(8.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(80.dp)) // Padding for bottom button
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeSelector(
    selectedEmployee: Employee?,
    onEmployeeSelected: (Employee) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedEmployee?.name ?: "Select Employee",
            onValueChange = {},
            readOnly = true,
            label = { Text("Assign To") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            shape = RoundedCornerShape(8.dp),
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            mockEmployees.forEach { employee ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(employee.name, fontWeight = FontWeight.Bold)
                            Text(employee.role, style = MaterialTheme.typography.bodySmall)
                        }
                    },
                    onClick = {
                        onEmployeeSelected(employee)
                        expanded = false
                    }
                )
            }
        }
    }
}
