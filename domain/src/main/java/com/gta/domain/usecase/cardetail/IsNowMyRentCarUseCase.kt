package com.gta.domain.usecase.cardetail

import com.gta.domain.repository.CarRepository
import com.gta.domain.repository.UserRepository
import javax.inject.Inject

class IsNowMyRentCarUseCase @Inject constructor(
    private val carRepository: CarRepository,
    private val userRepository: UserRepository
) {
    operator fun invoke(carId: String): Boolean {
        return carRepository.getNowRentUser(carId) == userRepository.getMyUserId()
    }
}
