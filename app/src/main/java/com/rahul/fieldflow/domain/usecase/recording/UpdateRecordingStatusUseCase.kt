package com.rahul.fieldflow.domain.usecase.recording

import com.rahul.fieldflow.domain.repository.RecordingRepository
import java.time.OffsetDateTime
import javax.inject.Inject

class UpdateRecordingStatusUseCase @Inject constructor(
    private val recordingRepository: RecordingRepository
) {
    suspend operator fun invoke(sessionId: String, status: String, endedAt: OffsetDateTime? = null, durationSeconds: Int? = null): Result<Unit> {
        return recordingRepository.updateSessionStatus(sessionId, status, endedAt, durationSeconds)
    }
}
