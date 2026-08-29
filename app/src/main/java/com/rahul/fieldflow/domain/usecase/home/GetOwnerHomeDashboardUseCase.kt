package com.rahul.fieldflow.domain.usecase.home

import com.rahul.fieldflow.domain.model.*
import com.rahul.fieldflow.domain.repository.*
import kotlinx.coroutines.flow.first
import java.time.OffsetDateTime
import javax.inject.Inject

data class OwnerHomeDashboard(
    val profile: UserProfile,
    val workspace: Workspace,
    val taskStats: TaskStats,
    val latestTasks: List<Task>,
    val latestReports: List<TaskReportContext>,
    val teamPreview: List<TeamMemberWithStats>
)

class GetOwnerHomeDashboardUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val workspaceRepository: WorkspaceRepository,
    private val taskRepository: TaskRepository,
    private val reportRepository: ReportRepository
) {
    suspend operator fun invoke(): Result<OwnerHomeDashboard> {
        val user = authRepository.currentUser.first() ?: return Result.failure(Exception("Not logged in"))
        val workspaceId = user.workspaceId ?: return Result.failure(Exception("No workspace found"))
        
        val workspaceResult = workspaceRepository.getWorkspaceById(workspaceId)
        val tasksResult = taskRepository.getOwnerTasks()
        val reportsResult = reportRepository.getOwnerReports(user.id)
        val employeesResult = authRepository.getTeamMembers(workspaceId)

        return workspaceResult.map { workspace ->
            val allTasks = tasksResult.getOrDefault(emptyList())
            val allReports = reportsResult.getOrDefault(emptyList())
            val allEmployees = employeesResult.getOrDefault(emptyList())

            val now = OffsetDateTime.now()
            
            val taskStats = TaskStats(
                totalCount = allTasks.size,
                activeCount = allTasks.count { it.status == TaskStatus.IN_PROGRESS },
                completedCount = allTasks.count { it.status == TaskStatus.COMPLETED },
                pendingCount = allTasks.count { it.status == TaskStatus.PENDING || it.status == TaskStatus.ASSIGNED },
                lateCount = allTasks.count { it.status != TaskStatus.COMPLETED && it.dueDate?.isBefore(now) == true }
            )

            val latestTasks = allTasks.sortedByDescending { it.createdAt }.take(3)
            val latestReports = allReports.sortedByDescending { it.aiReport?.createdAt }.take(4)
            
            val teamPreview = allEmployees.filter { it.role == UserRole.EMPLOYEE }.map { employee ->
                val employeeTasks = allTasks.filter { it.assignedEmployee?.id == employee.id }
                TeamMemberWithStats(
                    employee = employee,
                    totalTasks = employeeTasks.size,
                    completedTasks = employeeTasks.count { it.status == TaskStatus.COMPLETED },
                    currentTask = employeeTasks.find { it.status == TaskStatus.IN_PROGRESS || it.status == TaskStatus.ASSIGNED }
                )
            }.take(5)

            OwnerHomeDashboard(
                profile = user,
                workspace = workspace,
                taskStats = taskStats,
                latestTasks = latestTasks,
                latestReports = latestReports,
                teamPreview = teamPreview
            )
        }
    }
}
