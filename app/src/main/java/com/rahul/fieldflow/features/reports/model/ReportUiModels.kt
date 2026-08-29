package com.rahul.fieldflow.features.reports.model

import com.rahul.fieldflow.domain.model.TaskReportContext
import com.rahul.fieldflow.features.tasks.model.Employee
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

enum class ReportStatus(val label: String) {
    PENDING("Pending"),
    PROCESSING("AI Processing"),
    COMPLETED("Completed"),
    FAILED("Report Failed"),
    NEEDS_REVIEW("Needs Review"),
    REVIEWED("Reviewed")
}

data class ReportProof(
    val type: ProofType,
    val count: Int,
    val description: String
)

enum class ProofType {
    PHOTO, DOC
}

data class ActionItem(
    val id: String,
    val description: String
)

data class Report(
    val id: String, // This is taskId for navigation
    val reportId: String, // This is the actual task_reports.id for updates
    val title: String,
    val employee: Employee,
    val date: LocalDateTime,
    val location: String,
    val status: ReportStatus,
    val isLocationVerified: Boolean,
    val voiceDuration: String,
    val isAiReady: Boolean,
    val photoCount: Int,
    val submittedTime: String,
    val transcript: String? = null,
    val actionItems: List<ActionItem> = emptyList(),
    val followUpDate: String? = null,
    val proofs: List<ReportProof> = emptyList()
)

fun TaskReportContext.toUiModel(): Report {
    val totalDurationSeconds = sessions.sumOf { it.durationSeconds ?: 0 }
    val minutes = totalDurationSeconds / 60
    val seconds = totalDurationSeconds % 60
    val durationStr = "%d:%02d".format(minutes, seconds)

    val completedAt = task.completedAt ?: task.createdAt
    val localDateTime = completedAt.toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()

    val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
    val submittedTime = localDateTime.format(timeFormatter)

    val reportStatus = when (aiReport?.status?.lowercase()) {
        "completed" -> ReportStatus.COMPLETED
        "processing" -> ReportStatus.PROCESSING
        "pending" -> ReportStatus.PENDING
        "failed" -> ReportStatus.FAILED
        "needs_review" -> ReportStatus.NEEDS_REVIEW
        "reviewed" -> ReportStatus.REVIEWED
        else -> ReportStatus.PENDING
    }

    return Report(
        id = task.id, // Use task ID for navigation
        reportId = aiReport?.id ?: "",
        title = task.title,
        employee = Employee(
            id = task.assignedEmployee?.id ?: "",
            name = task.assignedEmployee?.fullName ?: "Unknown",
            role = task.assignedEmployee?.role?.name ?: ""
        ),
        date = localDateTime,
        location = task.location ?: "Unknown Location",
        status = reportStatus,
        isLocationVerified = true,
        voiceDuration = durationStr,
        isAiReady = reportStatus == ReportStatus.COMPLETED,
        photoCount = 0,
        submittedTime = submittedTime,
        transcript = transcripts.firstOrNull()?.text,
        actionItems = aiReport?.actionItems?.map { 
            ActionItem(it.title, it.description) 
        } ?: emptyList()
    )
}
