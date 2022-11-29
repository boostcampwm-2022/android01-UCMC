package com.gta.domain.usecase.car

import com.gta.domain.model.SimpleCar
import com.gta.domain.repository.CarRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSimpleCarUseCase @Inject constructor(
    private val carRepository: CarRepository
) {
    operator fun invoke(carId: String): Flow<SimpleCar> =
        carRepository.getSimpleCar(carId)
}
