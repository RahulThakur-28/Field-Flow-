package com.rahul.fieldflow.features.tasks.owner.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.rahul.fieldflow.features.tasks.model.Employee
import com.rahul.fieldflow.features.tasks.model.TaskPriority
import com.rahul.fieldflow.ui.theme.PrimaryBlue
import com.rahul.fieldflow.ui.theme.TextDark
import com.rahul.fieldflow.ui.theme.TextSecondary
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskForm(
    title: String,
    onTitleChange: (String) -> Unit,
    titleError: String? = null,
    description: String,
    onDescriptionChange: (String) -> Unit,
    descriptionError: String? = null,
    location: String,
    onLocationChange: (String) -> Unit,
    locationError: String? = null,
    selectedEmployee: Employee?,
    employees: List<Employee>,
    onEmployeeSelected: (Employee) -> Unit,
    employeeError: String? = null,
    priority: TaskPriority,
    onPriorityChange: (TaskPriority) -> Unit,
    date: LocalDate?,
    onDateChange: (LocalDate) -> Unit,
    dateError: String? = null,
    startTime: LocalTime?,
    onStartTimeChange: (LocalTime) -> Unit,
    startTimeError: String? = null,
    deadline: LocalTime?,
    onDeadlineChange: (LocalTime) -> Unit,
    deadlineError: String? = null,
    instructions: String,
    onInstructionsChange: (String) -> Unit,
    checklist: List<String>,
    onAddChecklistItem: (String) -> Unit,
    onRemoveChecklistItem: (String) -> Unit,
    onPickOnMap: () -> Unit,
    generalError: String? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        FormSection(title = "BASIC INFORMATION") {
            AppFormField(
                label = "Task Title *",
                error = titleError
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = onTitleChange,
                    placeholder = { Text("e.g., Client Site Visit") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    isError = titleError != null,
                    singleLine = true
                )
            }

            AppFormField(
                label = "Description *",
                error = descriptionError
            ) {
                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    placeholder = { Text("Describe the work to be done...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    isError = descriptionError != null,
                    minLines = 3
                )
            }
        }

        FormSection(title = "ASSIGNMENT & PRIORITY") {
            AppFormField(
                label = "Assign Employee *",
                error = employeeError
            ) {
                EmployeeSelector(
                    selectedEmployee = selectedEmployee,
                    employees = employees,
                    onEmployeeSelected = onEmployeeSelected,
                    isError = employeeError != null
                )
            }

            AppFormField(label = "Priority") {
                PrioritySelector(
                    selectedPriority = priority,
                    onPriorityChange = onPriorityChange
                )
            }
        }

        FormSection(title = "LOCATION") {
            AppFormField(
                label = "Destination *",
                error = locationError
            ) {
                OutlinedTextField(
                    value = location,
                    onValueChange = onLocationChange,
                    placeholder = { Text("Enter address or site name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    isError = locationError != null,
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = PrimaryBlue) },
                    trailingIcon = {
                        IconButton(onClick = onPickOnMap) {
                            Icon(Icons.Default.Map, contentDescription = "Pick on map", tint = PrimaryBlue)
                        }
                    }
                )
            }
        }

        FormSection(title = "SCHEDULE") {
            AppFormField(
                label = "Date *",
                error = dateError
            ) {
                DatePickerField(
                    date = date,
                    onDateSelected = onDateChange,
                    isError = dateError != null
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AppFormField(
                    label = "Start Time *",
                    error = startTimeError,
                    modifier = Modifier.weight(1f)
                ) {
                    TimePickerField(
                        time = startTime,
                        onTimeSelected = onStartTimeChange,
                        isError = startTimeError != null
                    )
                }

                AppFormField(
                    label = "Deadline *",
                    error = deadlineError,
                    modifier = Modifier.weight(1f)
                ) {
                    TimePickerField(
                        time = deadline,
                        onTimeSelected = onDeadlineChange,
                        isError = deadlineError != null
                    )
                }
            }
        }

        FormSection(title = "INSTRUCTIONS") {
            AppFormField(label = "Work Instructions") {
                OutlinedTextField(
                    value = instructions,
                    onValueChange = onInstructionsChange,
                    placeholder = { Text("Additional details, tools required, etc.") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 2
                )
            }
        }

        FormSection(title = "CHECKLIST") {
            ChecklistSection(
                items = checklist,
                onAddItem = onAddChecklistItem,
                onRemoveItem = onRemoveChecklistItem
            )
        }

        if (generalError != null) {
            Text(
                text = generalError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun FormSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = TextSecondary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        content()
    }
}

@Composable
fun AppFormField(
    label: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextDark
        )
        content()
        if (error != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
            ) {
                Icon(
                    Icons.Default.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeSelector(
    selectedEmployee: Employee?,
    employees: List<Employee>,
    onEmployeeSelected: (Employee) -> Unit,
    isError: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedEmployee?.name ?: "Select an employee",
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            shape = RoundedCornerShape(12.dp),
            isError = isError,
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = TextSecondary) },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            employees.forEach { employee ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier.size(32.dp),
                                shape = CircleShape,
                                color = PrimaryBlue.copy(alpha = 0.1f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = employee.name.take(1).uppercase(),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = PrimaryBlue,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(employee.name, fontWeight = FontWeight.Bold)
                                Text(employee.role, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
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

@Composable
fun PrioritySelector(
    selectedPriority: TaskPriority,
    onPriorityChange: (TaskPriority) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TaskPriority.values().forEach { priority ->
            val isSelected = selectedPriority == priority
            val config = getPriorityConfig(priority)
            
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clickable { onPriorityChange(priority) },
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) config.backgroundColor else Color.White,
                border = BorderStroke(
                    width = if (isSelected) 1.5.dp else 1.dp,
                    color = if (isSelected) config.color else Color.LightGray.copy(alpha = 0.5f)
                )
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = priority.label,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isSelected) config.color else TextSecondary,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

data class PriorityUiConfig(val color: Color, val backgroundColor: Color)

@Composable
fun getPriorityConfig(priority: TaskPriority): PriorityUiConfig {
    return when (priority) {
        TaskPriority.LOW -> PriorityUiConfig(Color(0xFF2E7D32), Color(0xFFE8F5E9))
        TaskPriority.MEDIUM -> PriorityUiConfig(Color(0xFFF9A825), Color(0xFFFFFDE7))
        TaskPriority.HIGH -> PriorityUiConfig(Color(0xFFEF6C00), Color(0xFFFFF3E0))
        TaskPriority.URGENT -> PriorityUiConfig(Color(0xFFC62828), Color(0xFFFFEBEE))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    date: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    isError: Boolean
) {
    var showDialog by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = date?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
    )

    OutlinedTextField(
        value = date?.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) ?: "",
        onValueChange = {},
        readOnly = true,
        placeholder = { Text("Select Date") },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true },
        enabled = false,
        shape = RoundedCornerShape(12.dp),
        isError = isError,
        leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, tint = TextSecondary) },
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = TextDark,
            disabledBorderColor = if (isError) MaterialTheme.colorScheme.error else Color.LightGray.copy(alpha = 0.5f),
            disabledPlaceholderColor = TextSecondary,
            disabledLeadingIconColor = TextSecondary,
            disabledContainerColor = Color.White
        )
    )

    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val localDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                        onDateSelected(localDate)
                    }
                    showDialog = false
                }) {
                    Text("OK", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerField(
    time: LocalTime?,
    onTimeSelected: (LocalTime) -> Unit,
    isError: Boolean
) {
    var showDialog by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(
        initialHour = time?.hour ?: 12,
        initialMinute = time?.minute ?: 0
    )

    OutlinedTextField(
        value = time?.format(DateTimeFormatter.ofPattern("hh:mm a")) ?: "",
        onValueChange = {},
        readOnly = true,
        placeholder = { Text("Select Time") },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true },
        enabled = false,
        shape = RoundedCornerShape(12.dp),
        isError = isError,
        leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null, tint = TextSecondary) },
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = TextDark,
            disabledBorderColor = if (isError) MaterialTheme.colorScheme.error else Color.LightGray.copy(alpha = 0.5f),
            disabledPlaceholderColor = TextSecondary,
            disabledLeadingIconColor = TextSecondary,
            disabledContainerColor = Color.White
        )
    )

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Select Time",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )
                    TimePicker(state = timePickerState)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Cancel")
                        }
                        TextButton(onClick = {
                            onTimeSelected(LocalTime.of(timePickerState.hour, timePickerState.minute))
                            showDialog = false
                        }) {
                            Text("OK", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChecklistSection(
    items: List<String>,
    onAddItem: (String) -> Unit,
    onRemoveItem: (String) -> Unit
) {
    var newItemText by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8F9FA), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = item, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                IconButton(onClick = { onRemoveItem(item) }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Remove", tint = TextSecondary, modifier = Modifier.size(16.dp))
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = newItemText,
                onValueChange = { newItemText = it },
                placeholder = { Text("Add task item...") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                trailingIcon = {
                    if (newItemText.isNotBlank()) {
                        IconButton(onClick = {
                            onAddItem(newItemText)
                            newItemText = ""
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Add", tint = PrimaryBlue)
                        }
                    }
                }
            )
        }
    }
}
