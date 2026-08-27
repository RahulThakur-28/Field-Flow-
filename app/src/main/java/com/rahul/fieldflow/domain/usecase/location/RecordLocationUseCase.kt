package com.rahul.fieldflow.domain.usecase.location

import com.rahul.fieldflow.domain.model.LocationPoint
import com.rahul.fieldflow.domain.repository.LocationRepository
import javax.inject.Inject

class RecordLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(point: LocationPoint): Result<Unit> {
        return locationRepository.recordLocationPoint(point)
    }
}
