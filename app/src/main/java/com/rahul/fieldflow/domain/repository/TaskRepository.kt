package com.rahul.fieldflow.domain.repository

import com.rahul.fieldflow.domain.model.Task
import com.rahul.fieldflow.domain.model.TaskPriority
import java.time.OffsetDateTime

interface TaskRepository {
    suspend fun createTask(
        title: String,
        description: String?,
        priority: TaskPriority,
        location: String?,
        dueDate: OffsetDateTime?,
        employeeId: String,
        latitude: Double? = null,
        longitude: Double? = null,
        radiusMeters: Int? = 100,
        checklistItems: List<String> = emptyList()
    ): Result<Unit>

    suspend fun getOwnerTasks(): Result<List<Task>>

    suspend fun getEmployeeTasks(): Result<List<Task>>

    suspend fun getTasksByEmployee(employeeId: String): Result<List<Task>>

    suspend fun getTaskById(taskId: String): Result<Task>

    suspend fun startTask(taskId: String): Result<Task>

    suspend fun completeTask(taskId: String): Result<Task>

    suspend fun updateChecklistItem(itemId: String, isCompleted: Boolean): Result<Unit>
}
