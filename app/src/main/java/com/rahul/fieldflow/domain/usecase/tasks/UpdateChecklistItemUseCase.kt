package com.rahul.fieldflow.domain.usecase.tasks

import com.rahul.fieldflow.domain.repository.TaskRepository
import javax.inject.Inject

class UpdateChecklistItemUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(itemId: String, isCompleted: Boolean): Result<Unit> {
        return taskRepository.updateChecklistItem(itemId, isCompleted)
    }
}
