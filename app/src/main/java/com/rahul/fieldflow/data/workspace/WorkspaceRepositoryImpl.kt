package com.rahul.fieldflow.data.workspace

import com.rahul.fieldflow.domain.model.Workspace
import com.rahul.fieldflow.domain.repository.WorkspaceRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkspaceRepositoryImpl @Inject constructor(
    private val workspaceDataSource: WorkspaceDataSource
) : WorkspaceRepository {

    override suspend fun getWorkspaceById(id: String): Result<Workspace> {
        return runCatching {
            workspaceDataSource.getWorkspaceById(id).toDomain()
        }
    }

    override suspend fun findWorkspaceByCode(code: String): Result<Workspace?> {
        return runCatching {
            workspaceDataSource.findWorkspaceByCode(code)?.toDomain()
        }
    }

    override suspend fun getWorkspaceByOwnerId(ownerId: String): Result<Workspace> {
        return runCatching {
            workspaceDataSource.getWorkspaceByOwnerId(ownerId).toDomain()
        }
    }
}
