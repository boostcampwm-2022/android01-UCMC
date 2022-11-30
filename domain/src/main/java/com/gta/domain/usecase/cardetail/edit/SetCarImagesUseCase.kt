package com.gta.domain.usecase.cardetail.edit

import com.gta.domain.repository.CarRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SetCarImagesUseCase @Inject constructor(
    private val carRepository: CarRepository
) {
    operator fun invoke(carId: String, images: List<String>): Flow<List<String>> {
        return carRepository.setCarImagesStorage(carId, images)
    }
}
