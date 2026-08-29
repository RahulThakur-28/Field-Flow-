package com.rahul.fieldflow.domain.usecase.recording

import com.rahul.fieldflow.domain.repository.RecordingRepository
import javax.inject.Inject

class TriggerTranscriptionUseCase @Inject constructor(
    private val recordingRepository: RecordingRepository
) {
    suspend operator fun invoke(sessionId: String): Result<Unit> {
        return recordingRepository.triggerTranscription(sessionId)
    }
}
