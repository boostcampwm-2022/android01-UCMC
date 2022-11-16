package com.gta.domain.usecase.cardetail

import com.gta.domain.repository.CarRepository
import javax.inject.Inject

class GetCarOwnerIdUseCase @Inject constructor(
    private val carRepository: CarRepository
) {
    operator fun invoke(carId: String): String {
        return carRepository.getOwnerId(carId)
    }
}
