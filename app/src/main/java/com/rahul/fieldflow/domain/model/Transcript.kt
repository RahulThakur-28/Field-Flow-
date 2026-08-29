package com.rahul.fieldflow.domain.model

import java.time.OffsetDateTime

data class Transcript(
    val id: String,
    val recordingSessionId: String,
    val text: String,
    val segments: List<TranscriptSegment>,
    val language: String?,
    val status: String,
    val createdAt: OffsetDateTime
)

data class TranscriptSegment(
    val start: Double,
    val end: Double,
    val text: String,
    val speaker: String? = null
)
