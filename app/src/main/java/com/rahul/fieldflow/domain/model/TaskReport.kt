package com.rahul.fieldflow.domain.model

import kotlinx.serialization.Serializable
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

@Serializable
data class KeyFinding(
    val title: String,
    val description: String
)

@Serializable
data class ActionItem(
    val title: String,
    val description: String,
    val priority: String
)
