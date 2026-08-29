package com.rahul.fieldflow.data.reports

import com.rahul.fieldflow.domain.model.ActionItem
import com.rahul.fieldflow.domain.model.KeyFinding
import com.rahul.fieldflow.domain.model.TaskReport
import com.rahul.fieldflow.domain.repository.ReportRepository
import kotlinx.serialization.json.*
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepositoryImpl @Inject constructor(
    private val reportDataSource: ReportDataSource
) : ReportRepository {

    override suspend fun getTaskReport(taskId: String): Result<TaskReport?> {
        return runCatching {
            reportDataSource.getTaskReport(taskId)?.toDomain()
        }
    }

    override suspend fun triggerReportGeneration(taskId: String): Result<Unit> {
        return runCatching {
            reportDataSource.triggerReportGeneration(taskId)
        }
    }

    private fun TaskReportDto.toDomain(): TaskReport {
        return TaskReport(
            id = id,
            taskId = taskId,
            summary = summary,
            keyFindings = keyFindings?.map { 
                val obj = it.jsonObject
                KeyFinding(
                    title = obj["title"]?.jsonPrimitive?.content ?: "",
                    description = obj["description"]?.jsonPrimitive?.content ?: ""
                )
            } ?: emptyList(),
            actionItems = actionItems?.map { 
                val obj = it.jsonObject
                ActionItem(
                    title = obj["title"]?.jsonPrimitive?.content ?: "",
                    description = obj["description"]?.jsonPrimitive?.content ?: "",
                    priority = obj["priority"]?.jsonPrimitive?.content ?: "medium"
                )
            } ?: emptyList(),
            status = status,
            version = version,
            createdAt = OffsetDateTime.parse(createdAt),
            updatedAt = OffsetDateTime.parse(updatedAt)
        )
    }
}
