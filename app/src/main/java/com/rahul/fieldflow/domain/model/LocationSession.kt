package com.rahul.fieldflow.domain.model

import java.time.OffsetDateTime

data class LocationSession(
    val id: String,
    val taskId: String,
    val employeeId: String,
    val status: String,
    val startedAt: OffsetDateTime,
    val endedAt: OffsetDateTime? = null
)
