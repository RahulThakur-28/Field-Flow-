package com.rahul.fieldflow.domain.usecase.location

import com.rahul.fieldflow.domain.repository.LocationRepository
import javax.inject.Inject

class StopTaskTrackingUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(sessionId: String): Result<Unit> {
        return locationRepository.stopLocationSession(sessionId)
    }
}
