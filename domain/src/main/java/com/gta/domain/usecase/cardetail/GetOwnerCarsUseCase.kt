package com.gta.domain.usecase.cardetail

import com.gta.domain.model.SimpleCar
import com.gta.domain.repository.CarRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class GetOwnerCarsUseCase @Inject constructor(
    private val carRepository: CarRepository
) {
    operator fun invoke(carId: String): Flow<SimpleCar> {
        return MutableStateFlow(SimpleCar("", "[말티즈] 멍멍", ""))
    }
}
