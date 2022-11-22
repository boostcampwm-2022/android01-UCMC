package com.gta.domain.usecase.cardetail

import com.gta.domain.repository.CarRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RemoveCarUseCase @Inject constructor(
    private val carRepository: CarRepository
) {
    operator fun invoke(userId: String, carId: String): Flow<Boolean> {
        return carRepository.removeCar(userId, carId)
    }
}
