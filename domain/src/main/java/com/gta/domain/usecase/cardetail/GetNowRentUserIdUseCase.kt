package com.gta.domain.usecase.cardetail

import com.gta.domain.repository.CarRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetNowRentUserIdUseCase @Inject constructor(
    private val carRepository: CarRepository
) {
    operator fun invoke(carId: String): Flow<String?> {
        return flowOf(null)
    }
}
