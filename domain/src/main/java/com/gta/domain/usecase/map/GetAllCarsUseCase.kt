package com.gta.domain.usecase.map

import com.gta.domain.model.SimpleCar
import com.gta.domain.repository.CarRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllCarsUseCase @Inject constructor(private val carRepository: CarRepository) {
    operator fun invoke(): Flow<List<SimpleCar>> {
        return carRepository.getAllCars()
    }
}
