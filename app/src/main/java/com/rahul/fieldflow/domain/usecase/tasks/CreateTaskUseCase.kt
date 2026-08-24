package com.rahul.fieldflow.domain.usecase.tasks

import com.rahul.fieldflow.domain.model.TaskPriority
import com.rahul.fieldflow.domain.repository.TaskRepository
import java.time.OffsetDateTime
import javax.inject.Inject

class CreateTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(
        title: String,
        description: String?,
        priority: TaskPriority,
        location: String?,
        dueDate: OffsetDateTime?,
        employeeId: String,
        latitude: Double? = null,
        longitude: Double? = null,
        radiusMeters: Int? = 100
    ): Result<Unit> {
        if (title.isBlank()) {
            return Result.failure(Exception("Title cannot be empty"))
        }
        return taskRepository.createTask(
            title = title,
            description = description,
            priority = priority,
            location = location,
            dueDate = dueDate,
            employeeId = employeeId,
            latitude = latitude,
            longitude = longitude,
            radiusMeters = radiusMeters
        )
    }
}
