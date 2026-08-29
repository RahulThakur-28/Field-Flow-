package com.rahul.fieldflow.domain.usecase.location

import com.rahul.fieldflow.domain.model.LocationSession
import com.rahul.fieldflow.domain.repository.LocationRepository
import javax.inject.Inject

class GetActiveLocationSessionUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(taskId: String): Result<LocationSession?> {
        return locationRepository.getActiveSession(taskId)
    }
}
