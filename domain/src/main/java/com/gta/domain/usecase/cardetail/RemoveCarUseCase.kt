package com.gta.domain.usecase.cardetail

import com.gta.domain.model.UCMCResult
import com.gta.domain.repository.CarRepository
import javax.inject.Inject

class RemoveCarUseCase @Inject constructor(
    private val carRepository: CarRepository
) {
    suspend operator fun invoke(userId: String, carId: String): UCMCResult<Unit> {
        return carRepository.removeCar(userId, carId)
    }
}
