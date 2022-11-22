package com.gta.domain.usecase.cardetail

import com.gta.domain.model.SimpleCar
import com.gta.domain.repository.CarRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOwnerCarsUseCase @Inject constructor(
    private val carRepository: CarRepository
) {
    operator fun invoke(ownerId: String): Flow<List<SimpleCar>> {
        return carRepository.getSimpleCarList(ownerId)
    }
}
