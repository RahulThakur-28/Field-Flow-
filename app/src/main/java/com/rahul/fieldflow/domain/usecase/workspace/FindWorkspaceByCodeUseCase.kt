package com.rahul.fieldflow.domain.usecase.workspace

import com.rahul.fieldflow.domain.model.Workspace
import com.rahul.fieldflow.domain.repository.WorkspaceRepository
import javax.inject.Inject

class FindWorkspaceByCodeUseCase @Inject constructor(
    private val workspaceRepository: WorkspaceRepository
) {
    suspend operator fun invoke(code: String): Result<Workspace?> {
        return workspaceRepository.findWorkspaceByCode(code)
    }
}
