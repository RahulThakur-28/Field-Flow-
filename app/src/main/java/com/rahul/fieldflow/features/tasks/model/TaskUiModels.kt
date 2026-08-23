package com.rahul.fieldflow.features.tasks.model

import java.time.LocalDateTime

enum class TaskStatus(val label: String) {
    PENDING("Pending"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    OVERDUE("Overdue"),
    CANCELLED("Cancelled")
}

enum class TaskPriority(val label: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High"),
    URGENT("Urgent")
}

data class ChecklistItem(
    val id: String,
    val title: String,
    val isChecked: Boolean = false
)

data class TaskTimelineEvent(
    val id: String,
    val status: TaskStatus,
    val timestamp: LocalDateTime,
    val description: String,
    val updatedBy: String
)

data class Employee(
    val id: String,
    val name: String,
    val role: String,
    val avatarUrl: String? = null
)

data class Task(
    val id: String,
    val title: String,
    val description: String,
    val status: TaskStatus,
    val priority: TaskPriority,
    val assignedTo: Employee,
    val location: String,
    val scheduledDate: LocalDateTime,
    val deadline: LocalDateTime? = null,
    val checklist: List<ChecklistItem> = emptyList(),
    val timeline: List<TaskTimelineEvent> = emptyList(),
    val progress: Float = 0f,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

val mockEmployees = listOf(
    Employee("1", "John Doe", "Field Technician"),
    Employee("2", "Jane Smith", "Electrician"),
    Employee("3", "Mike Johnson", "Plumber"),
    Employee("4", "Sarah Wilson", "Inspector")
)

val mockTasks = listOf(
    Task(
        id = "1",
        title = "AC Repair - Building A",
        description = "Fix the central cooling system in the main lobby. The compressor seems to be overheating.",
        status = TaskStatus.IN_PROGRESS,
        priority = TaskPriority.HIGH,
        assignedTo = mockEmployees[0],
        location = "123 Business Park, New York",
        scheduledDate = LocalDateTime.now().plusHours(2),
        checklist = listOf(
            ChecklistItem("c1", "Inspect Compressor", true),
            ChecklistItem("c2", "Check Refrigerant Levels", false),
            ChecklistItem("c3", "Replace Filter", false)
        ),
        progress = 0.33f
    ),
    Task(
        id = "2",
        title = "Annual Safety Inspection",
        description = "Perform a complete safety audit of the manufacturing floor as per OSHA standards.",
        status = TaskStatus.PENDING,
        priority = TaskPriority.MEDIUM,
        assignedTo = mockEmployees[1],
        location = "456 Industrial Way, New Jersey",
        scheduledDate = LocalDateTime.now().plusDays(1),
        progress = 0f
    ),
    Task(
        id = "3",
        title = "Emergency Pipe Leak",
        description = "Urgent: Water leakage reported in the server room basement.",
        status = TaskStatus.OVERDUE,
        priority = TaskPriority.URGENT,
        assignedTo = mockEmployees[2],
        location = "789 Tech Drive, Brooklyn",
        scheduledDate = LocalDateTime.now().minusHours(5),
        progress = 0f
    )
)
