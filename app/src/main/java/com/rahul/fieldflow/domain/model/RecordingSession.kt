package com.rahul.fieldflow.domain.model

import java.time.OffsetDateTime

data class RecordingSession(
    val id: String,
    val taskId: String,
    val employeeId: String,
    val startedAt: OffsetDateTime,
    val endedAt: OffsetDateTime? = null,
    val status: String,
    val storagePath: String? = null,
    val durationSeconds: Int? = null
)
