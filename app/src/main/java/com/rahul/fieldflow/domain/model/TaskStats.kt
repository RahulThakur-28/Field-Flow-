package com.rahul.fieldflow.domain.model

data class TaskStats(
    val totalCount: Int,
    val activeCount: Int,
    val completedCount: Int,
    val pendingCount: Int,
    val lateCount: Int
)
