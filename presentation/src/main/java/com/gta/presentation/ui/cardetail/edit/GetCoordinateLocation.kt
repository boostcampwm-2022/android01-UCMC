package com.gta.presentation.ui.cardetail.edit

import com.gta.domain.model.LocationInfo
import com.gta.domain.repository.MapRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCoordinateLocation @Inject constructor(
    private val mapRepository: MapRepository
) {
    operator fun invoke(longitude: Double, latitude: Double): Flow<String?> {
        return mapRepository.getSearchCoordinate(longitude.toString(), latitude.toString())
    }
}