package com.rahul.fieldflow.data.reports

import com.rahul.fieldflow.domain.model.*
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
            val dto = reportDataSource.getTaskReport(taskId)
            android.util.Log.d("REPORT_DEBUG", "REPORT_REPOSITORY_MAPPING taskId=$taskId dtoFound=${dto != null}")
            dto?.toDomain()
        }
    }

    override suspend fun triggerReportGeneration(taskId: String): Result<Unit> {
        return runCatching {
            reportDataSource.triggerReportGeneration(taskId)
        }
    }

    override suspend fun getEmployeeReports(): Result<List<TaskReportContext>> {
        return runCatching {
            reportDataSource.getEmployeeReports().map { dto ->
                val taskDomain = dto.tasks?.toDomain() ?: throw Exception("Task data missing for report ${dto.id}")
                TaskReportContext(
                    task = taskDomain,
                    sessions = dto.tasks.recordingSessions.map { sessionDto ->
                        RecordingSession(
                            id = sessionDto.id,
                            taskId = sessionDto.taskId,
                            employeeId = sessionDto.employeeId,
                            startedAt = OffsetDateTime.parse(sessionDto.startedAt),
                            endedAt = sessionDto.endedAt?.let { OffsetDateTime.parse(it) },
                            status = sessionDto.status,
                            storagePath = sessionDto.storagePath,
                            durationSeconds = sessionDto.durationSeconds
                        )
                    },
                    transcripts = emptyList(), // Not needed for list view
                    aiReport = dto.toDomain()
                )
            }
        }
    }

    private fun TaskReportWithDetailsDto.toDomain(): TaskReport {
        val formatter = java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME
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
            createdAt = try { OffsetDateTime.parse(createdAt) } catch (e: Exception) { try { OffsetDateTime.parse(createdAt, formatter) } catch (e2: Exception) { OffsetDateTime.now() } },
            updatedAt = try { OffsetDateTime.parse(updatedAt) } catch (e: Exception) { try { OffsetDateTime.parse(updatedAt, formatter) } catch (e2: Exception) { OffsetDateTime.now() } }
        )
    }

    private fun TaskReportDto.toDomain(): TaskReport {
        val formatter = java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME
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
            createdAt = try { OffsetDateTime.parse(createdAt) } catch (e: Exception) { try { OffsetDateTime.parse(createdAt, formatter) } catch (e2: Exception) { OffsetDateTime.now() } },
            updatedAt = try { OffsetDateTime.parse(updatedAt) } catch (e: Exception) { try { OffsetDateTime.parse(updatedAt, formatter) } catch (e2: Exception) { OffsetDateTime.now() } }
        )
    }
}
