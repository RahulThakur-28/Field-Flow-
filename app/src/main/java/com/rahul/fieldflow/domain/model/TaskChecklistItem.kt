package com.rahul.fieldflow.domain.model

data class TaskChecklistItem(
    val id: String,
    val taskId: String,
    val itemText: String,
    val isCompleted: Boolean,
    val position: Int
)
