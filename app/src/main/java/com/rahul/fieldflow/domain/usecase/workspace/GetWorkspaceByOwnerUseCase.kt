package com.rahul.fieldflow.domain.usecase.workspace

import com.rahul.fieldflow.domain.model.Workspace
import com.rahul.fieldflow.domain.repository.WorkspaceRepository
import javax.inject.Inject

class GetWorkspaceByOwnerUseCase @Inject constructor(
    private val workspaceRepository: WorkspaceRepository
) {
    suspend operator fun invoke(ownerId: String): Result<Workspace> {
        return workspaceRepository.getWorkspaceByOwnerId(ownerId)
    }
}
