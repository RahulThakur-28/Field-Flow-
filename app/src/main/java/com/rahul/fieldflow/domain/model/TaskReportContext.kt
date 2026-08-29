package com.rahul.fieldflow.domain.model

data class TaskReportContext(
    val task: Task,
    val sessions: List<RecordingSession>,
    val transcripts: List<Transcript>,
    val aiReport: TaskReport?
)
