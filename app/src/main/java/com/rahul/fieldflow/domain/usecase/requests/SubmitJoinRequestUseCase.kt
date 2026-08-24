package com.rahul.fieldflow.domain.usecase.requests

import com.rahul.fieldflow.domain.repository.JoinRequestRepository
import javax.inject.Inject

class SubmitJoinRequestUseCase @Inject constructor(
    private val joinRequestRepository: JoinRequestRepository
) {
    suspend operator fun invoke(workspaceId: String): Result<Unit> {
        return joinRequestRepository.submitRequest(workspaceId)
    }
}
