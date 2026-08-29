package com.rahul.fieldflow.domain.usecase.reports

import com.rahul.fieldflow.domain.model.TaskReportContext
import com.rahul.fieldflow.domain.repository.AuthRepository
import com.rahul.fieldflow.domain.repository.ReportRepository
import javax.inject.Inject

class GetOwnerReportsUseCase @Inject constructor(
    private val reportRepository: ReportRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<List<TaskReportContext>> {
        val ownerId = authRepository.getCurrentSession() ?: return Result.failure(Exception("No active session"))
        return reportRepository.getOwnerReports(ownerId)
    }
}
