package com.gta.domain.usecase.map

import com.gta.domain.model.Coordinate
import com.gta.domain.model.SimpleCar
import com.gta.domain.repository.CarRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNearCarsUseCase @Inject constructor(private val carRepository: CarRepository) {
    operator fun invoke(min: Coordinate, max: Coordinate): Flow<List<SimpleCar>> {
        return carRepository.getNearCars(min, max)
    }
}
