package com.rahul.fieldflow.domain.usecase.requests

import com.rahul.fieldflow.domain.repository.JoinRequestRepository
import javax.inject.Inject

class RespondToJoinRequestUseCase @Inject constructor(
    private val joinRequestRepository: JoinRequestRepository
) {
    suspend operator fun invoke(requestId: String, approve: Boolean): Result<Unit> {
        return joinRequestRepository.respondToRequest(requestId, approve)
    }
}
