package com.rahul.fieldflow.domain.usecase.home

import com.rahul.fieldflow.domain.model.*
import com.rahul.fieldflow.domain.repository.*
import kotlinx.coroutines.flow.first
import java.time.OffsetDateTime
import javax.inject.Inject

class GetEmployeeHomeDashboardUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val taskRepository: TaskRepository,
    private val reportRepository: ReportRepository,
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(): Result<EmployeeHomeDashboard> {
        val user = authRepository.currentUser.first() ?: return Result.failure(Exception("Not logged in"))
        
        val tasksResult = taskRepository.getEmployeeTasks()
        val reportsResult = reportRepository.getEmployeeReports(user.id)
        val notificationsResult = notificationRepository.getUnreadCount()

        return tasksResult.map { allTasks ->
            val allReports = reportsResult.getOrDefault(emptyList())
            val unreadCount = notificationsResult.getOrDefault(0)

            val now = OffsetDateTime.now()
            
            val taskStats = TaskStats(
                totalCount = allTasks.size,
                activeCount = allTasks.count { it.status == TaskStatus.IN_PROGRESS },
                completedCount = allTasks.count { it.status == TaskStatus.COMPLETED },
                pendingCount = allTasks.count { it.status == TaskStatus.PENDING || it.status == TaskStatus.ASSIGNED },
                lateCount = allTasks.count { it.status != TaskStatus.COMPLETED && it.dueDate?.isBefore(now) == true }
            )

            val nextTask = allTasks
                .filter { it.status == TaskStatus.IN_PROGRESS || it.status == TaskStatus.ASSIGNED || it.status == TaskStatus.PENDING }
                .minByOrNull { it.dueDate ?: it.createdAt }

            val upcomingTasks = allTasks
                .filter { it.status != TaskStatus.COMPLETED && it.id != nextTask?.id }
                .sortedBy { it.dueDate ?: it.createdAt }
                .take(3)

            val recentReports = allReports
                .sortedByDescending { it.aiReport?.createdAt }
                .take(3)

            EmployeeHomeDashboard(
                profile = user,
                taskStats = taskStats,
                nextTask = nextTask,
                upcomingTasks = upcomingTasks,
                recentReports = recentReports,
                unreadNotificationsCount = unreadCount
            )
        }
    }
}
