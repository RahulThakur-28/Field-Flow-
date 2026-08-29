package com.rahul.fieldflow.features.tasks.employee.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.fieldflow.core.audio.RecordingManager
import com.rahul.fieldflow.core.location.LocationManager
import com.rahul.fieldflow.domain.model.LocationPoint
import com.rahul.fieldflow.domain.model.LocationSession
import com.rahul.fieldflow.domain.repository.TaskRepository
import com.rahul.fieldflow.domain.usecase.location.GetActiveLocationSessionUseCase
import com.rahul.fieldflow.domain.usecase.location.RecordLocationUseCase
import com.rahul.fieldflow.domain.usecase.location.StartTaskTrackingUseCase
import com.rahul.fieldflow.domain.usecase.location.StopTaskTrackingUseCase
import com.rahul.fieldflow.domain.usecase.recording.StartRecordingUseCase
import com.rahul.fieldflow.domain.usecase.recording.StopRecordingUseCase
import com.rahul.fieldflow.domain.usecase.tasks.CompleteTaskUseCase
import com.rahul.fieldflow.domain.usecase.tasks.StartTaskUseCase
import com.rahul.fieldflow.domain.usecase.tasks.UpdateChecklistItemUseCase
import com.rahul.fieldflow.features.tasks.employee.state.EmployeeTaskDetailsUiState
import com.rahul.fieldflow.features.tasks.model.ChecklistItem
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
    private val stopTaskTrackingUseCase: StopTaskTrackingUseCase,
    private val startTaskUseCase: StartTaskUseCase,
    private val completeTaskUseCase: CompleteTaskUseCase,
    private val updateChecklistItemUseCase: UpdateChecklistItemUseCase,
    private val startRecordingUseCase: StartRecordingUseCase,
    private val stopRecordingUseCase: StopRecordingUseCase,
    private val recordingManager: RecordingManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmployeeTaskDetailsUiState())
    val uiState: StateFlow<EmployeeTaskDetailsUiState> = _uiState.asStateFlow()

    private var locationJob: Job? = null
    private var activeSession: LocationSession? = null

    init {
        observeRecordingState()
    }

    private fun observeRecordingState() {
        recordingManager.state
            .onEach { state ->
                _uiState.update { it.copy(recordingState = state) }
            }
            .launchIn(viewModelScope)
    }

    fun loadTask(taskId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            taskRepository.getTaskById(taskId).onSuccess { task ->
                val uiStatus = if (task.status != com.rahul.fieldflow.domain.model.TaskStatus.COMPLETED && 
                    task.dueDate?.isBefore(OffsetDateTime.now()) == true) {
                    com.rahul.fieldflow.features.tasks.model.TaskStatus.OVERDUE
                } else {
                    try {
                        com.rahul.fieldflow.features.tasks.model.TaskStatus.valueOf(task.status.name)
                    } catch (e: Exception) {
                        com.rahul.fieldflow.features.tasks.model.TaskStatus.PENDING
                    }
                }

                val uiTask = com.rahul.fieldflow.features.tasks.model.Task(
                    id = task.id,
                    title = task.title,
                    description = task.description ?: "",
                    status = uiStatus,
                    priority = try {
                        com.rahul.fieldflow.features.tasks.model.TaskPriority.valueOf(task.priority.name)
                    } catch (e: Exception) {
                        com.rahul.fieldflow.features.tasks.model.TaskPriority.MEDIUM
                    },
                    assignedTo = com.rahul.fieldflow.features.tasks.model.Employee(
                        id = task.assignedEmployee?.id ?: "",
                        name = task.assignedEmployee?.fullName ?: "",
                        role = "Employee"
                    ),
                    location = task.location ?: "Unknown",
                    latitude = task.latitude,
                    longitude = task.longitude,
                    radiusMeters = task.radiusMeters,
                    scheduledDate = task.dueDate ?: task.createdAt,
                    checklist = task.checklist.map { 
                        ChecklistItem(it.id, it.itemText, it.isCompleted)
                    }
                )
                _uiState.update { it.copy(task = uiTask, isLoading = false) }
                
                checkActiveSession(taskId)
                startLocationObservation()
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message) }
            }
        }
    }

    fun toggleChecklistItem(itemId: String, isChecked: Boolean) {
        val task = _uiState.value.task ?: return
        
        // Optimistic UI update
        val updatedChecklist = task.checklist.map {
            if (it.id == itemId) it.copy(isChecked = isChecked) else it
        }
        _uiState.update { it.copy(task = task.copy(checklist = updatedChecklist)) }

        viewModelScope.launch {
            updateChecklistItemUseCase(itemId, isChecked).onFailure { error ->
                // Revert on failure
                _uiState.update { it.copy(task = task, error = "Failed to update checklist: ${error.message}") }
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

        Log.d("RECORD_DEBUG", "START_BUTTON_CLICKED taskId=${task.id} taskStatus=${task.status} isInsideGeofence=${state.isInsideGeofence}")

        if (!state.isInsideGeofence) {
            _uiState.update { it.copy(error = "You are outside the task location. Move within the ${task.radiusMeters}m geofence to start.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            // 1. Call RPC to update status in DB
            Log.d("RECORD_DEBUG", "CALLING_START_TASK")
            startTaskUseCase(task.id).onSuccess { returnedTask ->
                Log.d("RECORD_DEBUG", "START_TASK_SUCCESS taskId=${task.id} returnedStatus=${returnedTask.status}")
                
                // 2. Start location tracking session
                Log.d("RECORD_DEBUG", "LOCATION_TRACKING_START_REQUESTED")
                startTaskTrackingUseCase(task.id).onSuccess { session ->
                    Log.d("RECORD_DEBUG", "LOCATION_SESSION_CREATED sessionId=${session.id}")
                    activeSession = session
                    
                    // 3. Start Recording
                    Log.d("RECORD_DEBUG", "CALLING_START_RECORDING_USE_CASE")
                    startRecordingUseCase(task.id)
                    Log.d("RECORD_DEBUG", "START_RECORDING_USE_CASE_RETURNED")

                    _uiState.update { 
                        it.copy(
                            isTrackingActive = true, 
                            isLoading = false,
                            task = task.copy(status = com.rahul.fieldflow.features.tasks.model.TaskStatus.IN_PROGRESS)
                        ) 
                    }
                    Log.d("RECORD_DEBUG", "LOCATION_TRACKING_STARTED")
                }.onFailure { error ->
                    Log.e("RECORD_DEBUG", "LOCATION_TRACKING_FAILED exception=${error.javaClass.simpleName} message=${error.message}")
                    _uiState.update { it.copy(isLoading = false, error = "Task started but tracking failed: ${error.message}") }
                }
            }.onFailure { error ->
                Log.e("RECORD_DEBUG", "START_FLOW_EXCEPTION stage=start_task exception=${error.javaClass.simpleName} message=${error.message}")
                _uiState.update { it.copy(isLoading = false, error = "Failed to start task: ${error.message}") }
            }
        }
    }

    fun stopTask() {
        val state = _uiState.value
        val task = state.task ?: return
        val session = activeSession

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // 1. Call RPC to complete task
            completeTaskUseCase(task.id).onSuccess {
                // 2. Stop tracking session if we have one
                session?.let { active ->
                    stopTaskTrackingUseCase(active.id).onSuccess {
                        Log.d("LOCATION_SESSION_DEBUG", "Session stopped: ${active.id}")
                    }.onFailure { error ->
                        Log.e("LOCATION_SESSION_DEBUG", "Failed to stop tracking session", error)
                    }
                }
                
                activeSession = null
                
                // 3. Stop Recording
                stopRecordingUseCase()

                _uiState.update { 
                    it.copy(
                        isTrackingActive = false, 
                        isLoading = false,
                        task = task.copy(status = com.rahul.fieldflow.features.tasks.model.TaskStatus.COMPLETED)
                    ) 
                }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, error = "Failed to complete task: ${error.message}") }
            }
        }
    }

    fun startRecordingManual() {
        val task = _uiState.value.task ?: return
        startRecordingUseCase(task.id)
    }

    override fun onCleared() {
        super.onCleared()
        locationJob?.cancel()
    }
}
