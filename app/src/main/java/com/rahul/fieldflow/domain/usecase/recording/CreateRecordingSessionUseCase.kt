package com.rahul.fieldflow.domain.usecase.recording

import com.rahul.fieldflow.domain.model.RecordingSession
import com.rahul.fieldflow.domain.repository.RecordingRepository
import javax.inject.Inject

class CreateRecordingSessionUseCase @Inject constructor(
    private val recordingRepository: RecordingRepository
) {
    suspend operator fun invoke(taskId: String): Result<RecordingSession> {
        return recordingRepository.createSession(taskId)
    }
}
