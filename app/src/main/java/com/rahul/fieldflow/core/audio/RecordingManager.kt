package com.rahul.fieldflow.core.audio

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton

enum class RecordingStatus {
    NOT_STARTED,
    RECORDING,
    INTERRUPTED,
    FAILED,
    COMPLETED
}

data class ActiveRecordingState(
    val status: RecordingStatus = RecordingStatus.NOT_STARTED,
    val taskId: String? = null,
    val sessionId: String? = null,
    val startedAt: OffsetDateTime? = null,
    val error: String? = null
)

@Singleton
class RecordingManager @Inject constructor() {
    private val _state = MutableStateFlow(ActiveRecordingState())
    val state: StateFlow<ActiveRecordingState> = _state.asStateFlow()

    fun updateState(
        status: RecordingStatus,
        taskId: String? = null,
        sessionId: String? = null,
        startedAt: OffsetDateTime? = null,
        error: String? = null
    ) {
        android.util.Log.d("RECORD_DEBUG", "RECORDING_STATE_CHANGED state=$status")
        _state.value = ActiveRecordingState(
            status = status,
            taskId = taskId ?: _state.value.taskId,
            sessionId = sessionId ?: _state.value.sessionId,
            startedAt = startedAt ?: _state.value.startedAt,
            error = error
        )
    }

    fun reset() {
        _state.value = ActiveRecordingState()
    }
}
