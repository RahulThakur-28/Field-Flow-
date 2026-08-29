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
    val id: String,
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
        else -> ReportStatus.PENDING
    }

    return Report(
        id = task.id, // Use task ID for navigation
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

val mockReports = listOf(
    Report(
        id = "1",
        title = "College Placement Visit",
        employee = Employee("1", "Rahul Thakur", "Field Employee"),
        date = LocalDateTime.of(2026, 8, 22, 12, 22),
        location = "ABC College, Andheri East",
        status = ReportStatus.NEEDS_REVIEW,
        isLocationVerified = true,
        voiceDuration = "4:32",
        isAiReady = true,
        photoCount = 3,
        submittedTime = "12:22 PM",
        transcript = "Visited the placement cell at ABC College today and met with the placement coordinator, Mr. Deepak Verma. We had a very productive discussion about internship opportunities for the upcoming batch. The college has approximately 340 students eligible for placements this year across engineering and management courses. Mr. Verma expressed strong interest in partnering with us...",
        actionItems = listOf(
            ActionItem("1", "Send company profile and brochure to coordinator"),
            ActionItem("2", "Prepare and share internship proposal document"),
            ActionItem("3", "Draft MOU for college review"),
            ActionItem("4", "Schedule HR team visit for 1–5 September 2026"),
            ActionItem("5", "Follow up with placement coordinator by 25 August")
        ),
        followUpDate = "25 August 2026",
        proofs = listOf(
            ReportProof(ProofType.PHOTO, 3, "High resolution"),
            ReportProof(ProofType.DOC, 2, "PDF format")
        )
    ),
    Report(
        id = "2",
        title = "Document Collection – HDFC",
        employee = Employee("1", "Rahul Thakur", "Field Employee"),
        date = LocalDateTime.of(2026, 8, 21, 10, 45),
        location = "HDFC Bank, Goregaon West",
        status = ReportStatus.REVIEWED,
        isLocationVerified = true,
        voiceDuration = "2:14",
        isAiReady = true,
        photoCount = 2,
        submittedTime = "10:45 AM"
    )
)
