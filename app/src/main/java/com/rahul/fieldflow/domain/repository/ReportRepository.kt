package com.rahul.fieldflow.domain.repository

import com.rahul.fieldflow.domain.model.TaskReport

interface ReportRepository {
    suspend fun getTaskReport(taskId: String): Result<TaskReport?>
    suspend fun triggerReportGeneration(taskId: String): Result<Unit>
}
