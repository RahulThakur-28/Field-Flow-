package com.rahul.fieldflow.domain.usecase.recording

import com.rahul.fieldflow.domain.repository.RecordingRepository
import javax.inject.Inject

class GetSignedUrlUseCase @Inject constructor(
    private val recordingRepository: RecordingRepository
) {
    suspend operator fun invoke(storagePath: String): Result<String> {
        return recordingRepository.getSignedUrl(storagePath)
    }
}
