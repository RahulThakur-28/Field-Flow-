package com.rahul.fieldflow.core.di

import com.rahul.fieldflow.data.requests.JoinRequestRepositoryImpl
import com.rahul.fieldflow.data.tasks.TaskRepositoryImpl
import com.rahul.fieldflow.data.workspace.WorkspaceRepositoryImpl
import com.rahul.fieldflow.domain.repository.JoinRequestRepository
import com.rahul.fieldflow.domain.repository.TaskRepository
import com.rahul.fieldflow.domain.repository.WorkspaceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWorkspaceRepository(
        workspaceRepositoryImpl: WorkspaceRepositoryImpl
    ): WorkspaceRepository

    @Binds
    @Singleton
    abstract fun bindJoinRequestRepository(
        joinRequestRepositoryImpl: JoinRequestRepositoryImpl
    ): JoinRequestRepository

    @Binds
    @Singleton
    abstract fun bindTaskRepository(
        taskRepositoryImpl: TaskRepositoryImpl
    ): TaskRepository
}
