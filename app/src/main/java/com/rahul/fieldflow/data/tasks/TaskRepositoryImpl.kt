package com.rahul.fieldflow.data.tasks

import android.util.Log
import com.rahul.fieldflow.data.auth.AuthDataSource
import com.rahul.fieldflow.domain.model.Task
import com.rahul.fieldflow.domain.model.TaskPriority
import com.rahul.fieldflow.domain.repository.TaskRepository
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val taskDataSource: TaskDataSource,
    private val authDataSource: AuthDataSource
) : TaskRepository {

    override suspend fun createTask(
        title: String,
        description: String?,
        priority: TaskPriority,
        location: String?,
        dueDate: OffsetDateTime?,
        employeeId: String,
        latitude: Double?,
        longitude: Double?,
        radiusMeters: Int?,
        checklistItems: List<String>
    ): Result<Unit> {
        return runCatching {
            val ownerId = authDataSource.getCurrentUserId()
                ?: throw Exception("Not authenticated")

            val formattedDueDate =
                dueDate?.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

            taskDataSource.createTask(
                title = title,
                description = description,
                priority = priority.name.lowercase(),
                createdBy = ownerId,
                location = location,
                dueDate = formattedDueDate,
                employeeId = employeeId,
                latitude = latitude,
                longitude = longitude,
                radiusMeters = radiusMeters,
                checklistItems = checklistItems
            )
        }
    }

    override suspend fun getOwnerTasks(): Result<List<Task>> {
        return runCatching {
            val ownerId = authDataSource.getCurrentUserId()
                ?: throw Exception("Not authenticated")
            
            Log.d("TEAM_DATA_DEBUG", "getOwnerTasks: ownerId = $ownerId")

            val taskDtos = taskDataSource.getTasksCreatedBy(ownerId)
            Log.d("TEAM_DATA_DEBUG", "getOwnerTasks: dto count = ${taskDtos.size}")
            
            taskDtos.map { it.toDomain() }
        }.onFailure { error ->
            Log.e("TEAM_DATA_DEBUG", "getOwnerTasks: failure = ${error.message}")
        }
    }

    override suspend fun getEmployeeTasks(): Result<List<Task>> {
        return runCatching {
            val employeeId = authDataSource.getCurrentUserId()
                ?: throw Exception("Not authenticated")

            Log.d("EMPLOYEE_TASK_TRACE", "getEmployeeTasks: authenticatedUserId = $employeeId")

            val taskDtos = taskDataSource.getTasksForEmployee(employeeId)
            Log.d("EMPLOYEE_TASK_TRACE", "getEmployeeTasks: decoded TaskDto count = ${taskDtos.size}")

            taskDtos.map { it.toDomain() }
        }.onFailure { error ->
            Log.e("EMPLOYEE_TASK_TRACE", "getEmployeeTasks: failure = ${error.message}")
        }
    }

    override suspend fun getTasksByEmployee(employeeId: String): Result<List<Task>> {
        return runCatching {
            Log.d("TEAM_DATA_DEBUG", "getTasksByEmployee: employeeId = $employeeId")
            val taskDtos = taskDataSource.getTasksForEmployee(employeeId)
            Log.d("TEAM_DATA_DEBUG", "getTasksByEmployee: dto count = ${taskDtos.size}")
            taskDtos.map { it.toDomain() }
        }.onFailure { error ->
            Log.e("TEAM_DATA_DEBUG", "getTasksByEmployee: failure = ${error.message}")
        }
    }

    override suspend fun getTaskById(taskId: String): Result<Task> {
        return runCatching {
            if (taskId.isBlank()) {
                throw Exception("Task ID cannot be empty")
            }

            authDataSource.getCurrentUserId()
                ?: throw Exception("Not authenticated")

            taskDataSource
                .getTaskById(taskId)
                .toDomain()
        }
    }

    override suspend fun startTask(taskId: String): Result<Task> {
        return runCatching {
            taskDataSource.startTask(taskId).toDomain()
        }
    }

    override suspend fun completeTask(taskId: String): Result<Task> {
        return runCatching {
            taskDataSource.completeTask(taskId).toDomain()
        }
    }

    override suspend fun updateChecklistItem(itemId: String, isCompleted: Boolean): Result<Unit> {
        return runCatching {
            taskDataSource.updateChecklistItemCompletion(itemId, isCompleted)
        }
    }
}