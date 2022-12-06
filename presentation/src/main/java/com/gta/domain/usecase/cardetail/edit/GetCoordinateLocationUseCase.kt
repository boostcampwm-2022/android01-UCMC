package com.gta.domain.usecase.cardetail.edit

import com.gta.domain.repository.MapRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCoordinateLocationUseCase @Inject constructor(
    private val mapRepository: MapRepository
) {
    operator fun invoke(longitude: Double, latitude: Double): Flow<String?> {
        return mapRepository.getSearchCoordinate(longitude.toString(), latitude.toString())
    }
}
