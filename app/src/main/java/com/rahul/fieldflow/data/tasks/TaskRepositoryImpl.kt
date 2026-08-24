package com.rahul.fieldflow.data.tasks

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
        radiusMeters: Int?
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
                radiusMeters = radiusMeters
            )
        }
    }

    override suspend fun getOwnerTasks(): Result<List<Task>> {
        return runCatching {
            val ownerId = authDataSource.getCurrentUserId()
                ?: throw Exception("Not authenticated")

            taskDataSource
                .getTasksCreatedBy(ownerId)
                .map { it.toDomain() }
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
}