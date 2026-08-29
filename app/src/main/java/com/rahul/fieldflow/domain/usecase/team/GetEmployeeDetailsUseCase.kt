package com.rahul.fieldflow.domain.usecase.team

import com.rahul.fieldflow.domain.model.EmployeeDetails
import com.rahul.fieldflow.domain.model.TaskStatus
import com.rahul.fieldflow.domain.repository.AuthRepository
import com.rahul.fieldflow.domain.repository.TaskRepository
import javax.inject.Inject
import android.util.Log

class GetEmployeeDetailsUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(employeeId: String): Result<EmployeeDetails> {
        Log.d("TEAM_DATA_DEBUG", "GetEmployeeDetailsUseCase: start employeeId=$employeeId")
        val profileResult = authRepository.getProfile(employeeId)
        val tasksResult = taskRepository.getTasksByEmployee(employeeId)
        
        return profileResult.map { profile ->
            val tasks = tasksResult.getOrDefault(emptyList())
            Log.d("TEAM_DATA_DEBUG", "GetEmployeeDetailsUseCase: tasks found count=${tasks.size}")
            
            tasks.forEach { task ->
                Log.d("TEAM_DATA_DEBUG", "Task: ${task.title}, status=${task.status}, hasReport=${task.hasReport}")
            }

            EmployeeDetails(
                profile = profile,
                currentTask = tasks.find { it.status == TaskStatus.IN_PROGRESS || it.status == TaskStatus.ASSIGNED },
                pastTasks = tasks.filter { it.status == TaskStatus.COMPLETED }.sortedByDescending { it.completedAt ?: it.createdAt }
            )
        }
    }
}
