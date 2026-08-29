package com.rahul.fieldflow.domain.usecase.reports

import com.rahul.fieldflow.domain.repository.ReportRepository
import javax.inject.Inject

class MarkReportAsReviewedUseCase @Inject constructor(
    private val reportRepository: ReportRepository
) {
    suspend operator fun invoke(reportId: String): Result<Unit> {
        return reportRepository.updateReportStatus(reportId, "reviewed")
    }
}
