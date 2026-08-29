package com.rahul.fieldflow.domain.usecase.recording

import com.rahul.fieldflow.domain.repository.RecordingRepository
import java.io.File
import javax.inject.Inject

class UploadRecordingUseCase @Inject constructor(
    private val recordingRepository: RecordingRepository
) {
    suspend operator fun invoke(taskId: String, sessionId: String, audioFile: File): Result<String> {
        return recordingRepository.uploadRecording(taskId, sessionId, audioFile)
    }
}
