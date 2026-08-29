package com.rahul.fieldflow.domain.usecase.reports

import com.rahul.fieldflow.domain.model.TaskReport
import com.rahul.fieldflow.domain.repository.ReportRepository
import javax.inject.Inject

class GetTaskReportUseCase @Inject constructor(
    private val reportRepository: ReportRepository
) {
    suspend operator fun invoke(taskId: String): Result<TaskReport?> {
        return reportRepository.getTaskReport(taskId)
    }
}
