package com.rahul.fieldflow.domain.repository

import com.rahul.fieldflow.domain.model.Workspace

interface WorkspaceRepository {
    suspend fun getWorkspaceById(id: String): Result<Workspace>
    suspend fun findWorkspaceByCode(code: String): Result<Workspace?>
    suspend fun getWorkspaceByOwnerId(ownerId: String): Result<Workspace>
}
