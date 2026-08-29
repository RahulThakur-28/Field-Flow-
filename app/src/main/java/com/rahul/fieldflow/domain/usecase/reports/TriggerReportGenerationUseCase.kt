package com.rahul.fieldflow.domain.usecase.reports

import com.rahul.fieldflow.domain.repository.ReportRepository
import javax.inject.Inject

class TriggerReportGenerationUseCase @Inject constructor(
    private val reportRepository: ReportRepository
) {
    suspend operator fun invoke(taskId: String): Result<Unit> {
        return reportRepository.triggerReportGeneration(taskId)
    }
}
