package com.rahul.fieldflow.domain.usecase.team

import com.rahul.fieldflow.domain.model.TeamMemberWithStats
import com.rahul.fieldflow.domain.model.UserProfile
import com.rahul.fieldflow.domain.model.UserRole
import com.rahul.fieldflow.domain.model.TaskStatus
import com.rahul.fieldflow.domain.repository.AuthRepository
import com.rahul.fieldflow.domain.repository.TaskRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import android.util.Log

class GetTeamWithStatsUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(): Result<List<TeamMemberWithStats>> {
        val user = authRepository.currentUser.first() ?: return Result.failure(Exception("Not logged in"))
        val workspaceId = user.workspaceId ?: return Result.failure(Exception("No workspace found"))
        
        Log.d("TEAM_DATA_DEBUG", "GetTeamWithStatsUseCase: workspaceId=$workspaceId")
        
        val employeesResult = authRepository.getTeamMembers(workspaceId)
        val tasksResult = taskRepository.getOwnerTasks()
        
        return employeesResult.map { employees ->
            val allTasks = tasksResult.getOrDefault(emptyList())
            Log.d("TEAM_DATA_DEBUG", "GetTeamWithStatsUseCase: allTasks count=${allTasks.size}")
            
            employees.filter { it.role == UserRole.EMPLOYEE }.map { employee ->
                val employeeTasks = allTasks.filter { task ->
                    val isAssigned = task.assignedEmployee?.id == employee.id
                    if (isAssigned) {
                        Log.d("TEAM_DATA_DEBUG", "Task assigned to ${employee.fullName}: ${task.title}")
                    }
                    isAssigned
                }
                
                Log.d("TEAM_DATA_DEBUG", "Employee ${employee.fullName}: totalTasks=${employeeTasks.size}")
                
                TeamMemberWithStats(
                    employee = employee,
                    totalTasks = employeeTasks.size,
                    completedTasks = employeeTasks.count { it.status == TaskStatus.COMPLETED },
                    currentTask = employeeTasks.find { it.status == TaskStatus.IN_PROGRESS || it.status == TaskStatus.ASSIGNED }
                )
            }
        }
    }
}
