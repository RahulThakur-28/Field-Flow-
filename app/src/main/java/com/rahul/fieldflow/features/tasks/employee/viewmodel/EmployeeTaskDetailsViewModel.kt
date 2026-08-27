package com.rahul.fieldflow.features.tasks.employee.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.fieldflow.core.location.LocationManager
import com.rahul.fieldflow.domain.model.LocationPoint
import com.rahul.fieldflow.domain.model.LocationSession
import com.rahul.fieldflow.domain.repository.TaskRepository
import com.rahul.fieldflow.domain.usecase.location.GetActiveLocationSessionUseCase
import com.rahul.fieldflow.domain.usecase.location.RecordLocationUseCase
import com.rahul.fieldflow.domain.usecase.location.StartTaskTrackingUseCase
import com.rahul.fieldflow.domain.usecase.location.StopTaskTrackingUseCase
import com.rahul.fieldflow.features.tasks.employee.state.EmployeeTaskDetailsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import javax.inject.Inject

@HiltViewModel
class EmployeeTaskDetailsViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val locationManager: LocationManager,
    private val startTaskTrackingUseCase: StartTaskTrackingUseCase,
    private val recordLocationUseCase: RecordLocationUseCase,
    private val getActiveLocationSessionUseCase: GetActiveLocationSessionUseCase,
    private val stopTaskTrackingUseCase: StopTaskTrackingUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmployeeTaskDetailsUiState())
    val uiState: StateFlow<EmployeeTaskDetailsUiState> = _uiState.asStateFlow()

    private var locationJob: Job? = null
    private var activeSession: LocationSession? = null

    fun loadTask(taskId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            taskRepository.getTaskById(taskId).onSuccess { task ->
                val uiTask = com.rahul.fieldflow.features.tasks.model.Task(
                    id = task.id,
                    title = task.title,
                    description = task.description ?: "",
                    status = com.rahul.fieldflow.features.tasks.model.TaskStatus.valueOf(task.status.name),
                    priority = com.rahul.fieldflow.features.tasks.model.TaskPriority.valueOf(task.priority.name),
                    assignedTo = com.rahul.fieldflow.features.tasks.model.Employee(
                        id = task.assignedEmployee?.id ?: "",
                        name = task.assignedEmployee?.fullName ?: "",
                        role = "Employee"
                    ),
                    location = task.location ?: "Unknown",
                    latitude = task.latitude,
                    longitude = task.longitude,
                    radiusMeters = task.radiusMeters,
                    scheduledDate = task.dueDate ?: task.createdAt
                )
                _uiState.update { it.copy(task = uiTask, isLoading = false) }
                
                checkActiveSession(taskId)
                startLocationObservation()
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message) }
            }
        }
    }

    private fun checkActiveSession(taskId: String) {
        viewModelScope.launch {
            getActiveLocationSessionUseCase(taskId).onSuccess { session ->
                activeSession = session
                _uiState.update { it.copy(isTrackingActive = session != null) }
            }
        }
    }

    private fun startLocationObservation() {
        locationJob?.cancel()
        locationJob = locationManager.getLocationUpdates(5000) // 5 seconds
            .onEach { location ->
                val task = _uiState.value.task ?: return@onEach
                val taskLat = task.latitude ?: return@onEach
                val taskLng = task.longitude ?: return@onEach
                
                val distance = locationManager.calculateDistance(
                    location.latitude, location.longitude,
                    taskLat, taskLng
                )
                
                val isInside = distance <= task.radiusMeters
                
                _uiState.update { 
                    it.copy(
                        isInsideGeofence = isInside,
                        distanceToDestination = distance
                    )
                }

                // If tracking is active, record the point
                activeSession?.let { session ->
                    val point = LocationPoint(
                        sessionId = session.id,
                        latitude = location.latitude,
                        longitude = location.longitude,
                        accuracy = location.accuracy,
                        altitude = location.altitude,
                        speed = location.speed,
                        recordedAt = OffsetDateTime.now()
                    )
                    recordLocationUseCase(point).onSuccess {
                        Log.d("LOCATION_POINT_DEBUG", "Point recorded for session ${session.id}")
                    }.onFailure { error ->
                        Log.e("LOCATION_POINT_DEBUG", "Failed to record point", error)
                    }
                }
                
                Log.d("GEOFENCE_DEBUG", "Task: ${task.id}, Dist: ${distance}m, Inside: $isInside")
            }
            .launchIn(viewModelScope)
    }

    fun startTask() {
        val state = _uiState.value
        val task = state.task ?: return

        if (!state.isInsideGeofence) {
            _uiState.update { it.copy(error = "You are outside the task location. Move within the ${task.radiusMeters}m geofence to start.") }
            return
        }

        viewModelScope.launch {
            startTaskTrackingUseCase(task.id).onSuccess { session ->
                activeSession = session
                _uiState.update { it.copy(isTrackingActive = true, error = null) }
                Log.d("LOCATION_SESSION_DEBUG", "Session started: ${session.id} for task ${task.id}")
            }.onFailure { error ->
                _uiState.update { it.copy(error = "Failed to start session: ${error.message}") }
            }
        }
    }

    fun stopTask() {
        val session = activeSession ?: return
        viewModelScope.launch {
            stopTaskTrackingUseCase(session.id).onSuccess {
                activeSession = null
                _uiState.update { it.copy(isTrackingActive = false, error = null) }
                Log.d("LOCATION_SESSION_DEBUG", "Session stopped: ${session.id}")
                
                // Refresh task to show completed status?
                loadTask(_uiState.value.task?.id ?: "")
            }.onFailure { error ->
                _uiState.update { it.copy(error = "Failed to stop session: ${error.message}") }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        locationJob?.cancel()
    }
}
