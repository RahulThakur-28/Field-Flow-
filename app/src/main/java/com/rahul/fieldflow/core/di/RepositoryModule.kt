package com.rahul.fieldflow.core.di

import com.rahul.fieldflow.data.location.LocationRepositoryImpl
import com.rahul.fieldflow.data.notifications.NotificationRepositoryImpl
import com.rahul.fieldflow.data.recording.RecordingRepositoryImpl
import com.rahul.fieldflow.data.reports.ReportRepositoryImpl
import com.rahul.fieldflow.data.requests.JoinRequestRepositoryImpl
import com.rahul.fieldflow.data.settings.SettingsRepositoryImpl
import com.rahul.fieldflow.data.tasks.TaskRepositoryImpl
import com.rahul.fieldflow.data.workspace.WorkspaceRepositoryImpl
import com.rahul.fieldflow.domain.repository.*
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

    @Binds
    @Singleton
    abstract fun bindLocationRepository(
        locationRepositoryImpl: LocationRepositoryImpl
    ): LocationRepository

    @Binds
    @Singleton
    abstract fun bindRecordingRepository(
        recordingRepositoryImpl: RecordingRepositoryImpl
    ): RecordingRepository

    @Binds
    @Singleton
    abstract fun bindReportRepository(
        reportRepositoryImpl: ReportRepositoryImpl
    ): ReportRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        notificationRepositoryImpl: NotificationRepositoryImpl
    ): NotificationRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        settingsRepositoryImpl: SettingsRepositoryImpl
    ): SettingsRepository
}
