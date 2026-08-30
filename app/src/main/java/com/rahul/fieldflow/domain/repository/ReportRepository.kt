package com.rahul.fieldflow.domain.repository

import com.rahul.fieldflow.domain.model.TaskReport
import com.rahul.fieldflow.domain.model.TaskReportContext

interface ReportRepository {
    suspend fun getTaskReport(taskId: String): Result<TaskReport?>
    suspend fun triggerReportGeneration(taskId: String): Result<Unit>
    suspend fun getEmployeeReports(userId: String): Result<List<TaskReportContext>>
    suspend fun getOwnerReports(workspaceId: String): Result<List<TaskReportContext>>
    suspend fun updateReportStatus(reportId: String, status: String): Result<Unit>
}
