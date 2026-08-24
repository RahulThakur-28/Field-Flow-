package com.rahul.fieldflow.domain.usecase.requests

import com.rahul.fieldflow.domain.model.JoinRequest
import com.rahul.fieldflow.domain.repository.JoinRequestRepository
import javax.inject.Inject

class GetPendingRequestsUseCase @Inject constructor(
    private val joinRequestRepository: JoinRequestRepository
) {
    suspend operator fun invoke(workspaceId: String): Result<List<JoinRequest>> {
        return joinRequestRepository.getPendingRequests(workspaceId)
    }
}
