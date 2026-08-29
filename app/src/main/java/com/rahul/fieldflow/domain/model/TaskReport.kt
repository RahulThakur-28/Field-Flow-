package com.rahul.fieldflow.domain.model

import java.time.OffsetDateTime

data class TaskReport(
    val id: String,
    val taskId: String,
    val summary: String?,
    val keyFindings: List<KeyFinding> = emptyList(),
    val actionItems: List<ActionItem> = emptyList(),
    val status: String,
    val version: Int,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

data class KeyFinding(
    val title: String,
    val description: String
)

data class ActionItem(
    val title: String,
    val description: String,
    val priority: String
)
