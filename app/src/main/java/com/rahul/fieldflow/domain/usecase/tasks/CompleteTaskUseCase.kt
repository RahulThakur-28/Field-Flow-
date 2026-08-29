package com.rahul.fieldflow.domain.usecase.tasks

import com.rahul.fieldflow.domain.model.Task
import com.rahul.fieldflow.domain.repository.TaskRepository
import javax.inject.Inject

class CompleteTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(taskId: String): Result<Task> {
        return taskRepository.completeTask(taskId)
    }
}
