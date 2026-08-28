package com.rahul.fieldflow.data.tasks

import com.rahul.fieldflow.domain.model.TaskChecklistItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskChecklistItemDto(
    @SerialName("id")
    val id: String? = null,
    @SerialName("task_id")
    val taskId: String? = null,
    @SerialName("item_text")
    val itemText: String? = null,
    @SerialName("is_completed")
    val isCompleted: Boolean = false,
    @SerialName("position")
    val position: Int = 0
) {
    fun toDomain() = TaskChecklistItem(
        id = id.orEmpty(),
        taskId = taskId.orEmpty(),
        itemText = itemText.orEmpty(),
        isCompleted = isCompleted,
        position = position
    )
}
