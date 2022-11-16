package com.gta.domain.usecase.cardetail

import com.gta.domain.repository.CarRepository
import javax.inject.Inject

class GetNowRentUserIdUseCase @Inject constructor(
    private val carRepository: CarRepository
) {
    operator fun invoke(carId: String): String? {
        return carRepository.getNowRentUser(carId)
    }
}
