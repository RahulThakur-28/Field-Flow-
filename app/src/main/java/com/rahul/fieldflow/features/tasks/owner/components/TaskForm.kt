package com.rahul.fieldflow.features.tasks.owner.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.rahul.fieldflow.features.tasks.model.Employee
import com.rahul.fieldflow.features.tasks.model.TaskPriority
import com.rahul.fieldflow.ui.theme.*
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
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
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
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
                    minLines = 3,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
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
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    trailingIcon = {
                        IconButton(onClick = onPickOnMap) {
                            Icon(Icons.Default.Map, contentDescription = "Pick on map", tint = MaterialTheme.colorScheme.primary)
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    )
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
                    minLines = 2,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
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
            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
            color = MaterialTheme.colorScheme.onBackground
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
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                disabledContainerColor = MaterialTheme.colorScheme.surface
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
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = employee.name.take(1).uppercase(),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(employee.name, fontWeight = FontWeight.Bold)
                                Text(employee.role, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                color = if (isSelected) config.backgroundColor else MaterialTheme.colorScheme.surface,
                border = BorderStroke(
                    width = if (isSelected) 1.5.dp else 1.dp,
                    color = if (isSelected) config.color else MaterialTheme.colorScheme.outlineVariant
                )
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = priority.label,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isSelected) config.color else MaterialTheme.colorScheme.onSurfaceVariant,
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
        TaskPriority.LOW -> PriorityUiConfig(SuccessGreen, SuccessGreen.copy(alpha = 0.15f))
        TaskPriority.MEDIUM -> PriorityUiConfig(WarningOrange, WarningOrange.copy(alpha = 0.15f))
        TaskPriority.HIGH -> PriorityUiConfig(Color(0xFFEF6C00), Color(0xFFFFF3E0).copy(alpha = 0.15f))
        TaskPriority.URGENT -> PriorityUiConfig(ErrorRed, ErrorRed.copy(alpha = 0.15f))
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
        leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledBorderColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outlineVariant,
            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledContainerColor = MaterialTheme.colorScheme.surface
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
        leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledBorderColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outlineVariant,
            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledContainerColor = MaterialTheme.colorScheme.surface
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
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = item, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                IconButton(onClick = { onRemoveItem(item) }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Remove", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
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
                            Icon(Icons.Default.Add, contentDescription = "Add", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            )
        }
    }
}
