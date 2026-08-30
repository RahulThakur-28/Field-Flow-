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
        val userId = authRepository.getCurrentSession() ?: return Result.failure(Exception("No active session"))
        val profile = authRepository.getProfile(userId).getOrNull() ?: return Result.failure(Exception("Profile not found"))
        
        val workspaceId = profile.workspaceId ?: return Result.failure(Exception("No workspace assigned"))
        
        android.util.Log.d("REPORT_DEBUG", "GET_OWNER_REPORTS_USECASE workspaceId=$workspaceId")
        return reportRepository.getOwnerReports(workspaceId)
    }
}
