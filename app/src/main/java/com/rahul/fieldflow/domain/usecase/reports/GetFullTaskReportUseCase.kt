package com.rahul.fieldflow.domain.usecase.reports

import com.rahul.fieldflow.domain.model.TaskReportContext
import com.rahul.fieldflow.domain.repository.RecordingRepository
import com.rahul.fieldflow.domain.repository.ReportRepository
import com.rahul.fieldflow.domain.repository.TaskRepository
import javax.inject.Inject

class GetFullTaskReportUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val recordingRepository: RecordingRepository,
    private val reportRepository: ReportRepository
) {
    suspend operator fun invoke(taskId: String): Result<TaskReportContext> {
        return runCatching {
            val task = taskRepository.getTaskById(taskId).getOrThrow()
            val sessions = recordingRepository.getSessionsByTaskId(taskId).getOrThrow()
            val transcripts = recordingRepository.getTranscriptsByTaskId(taskId).getOrThrow()
            val aiReport = reportRepository.getTaskReport(taskId).getOrThrow()

            TaskReportContext(
                task = task,
                sessions = sessions,
                transcripts = transcripts,
                aiReport = aiReport
            )
        }
    }
}
