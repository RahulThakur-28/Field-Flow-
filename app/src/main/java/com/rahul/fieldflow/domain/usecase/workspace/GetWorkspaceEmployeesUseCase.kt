package com.rahul.fieldflow.domain.usecase.workspace

import com.rahul.fieldflow.domain.model.UserProfile
import com.rahul.fieldflow.domain.model.UserRole
import com.rahul.fieldflow.domain.repository.AuthRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetWorkspaceEmployeesUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<List<UserProfile>> {
        // Refresh profile to get the latest workspace_id (in case of recent approval)
        val user = authRepository.refreshProfile().getOrNull() ?: authRepository.currentUser.first()
        
        if (user == null) return Result.failure(Exception("Not logged in"))
        val workspaceId = user.workspaceId ?: return Result.failure(Exception("No workspace found"))
        
        return authRepository.getTeamMembers(workspaceId).map { members ->
            members.filter { it.role == UserRole.EMPLOYEE }
        }
    }
}
