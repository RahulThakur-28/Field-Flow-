package com.rahul.fieldflow.domain.model

import java.time.OffsetDateTime

data class LocationPoint(
    val id: Long? = null,
    val sessionId: String,
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float? = null,
    val altitude: Double? = null,
    val speed: Float? = null,
    val recordedAt: OffsetDateTime
)
