package com.gta.domain.usecase.cardetail

import com.gta.domain.repository.CarRepository
import com.gta.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

enum class UseState {
    OWNER, NOW_RENT_USER, USER, NOT_AVAILABLE
}

class GetUseStateAboutCarUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val carRepository: CarRepository
) {
    operator fun invoke(carId: String): Flow<UseState> {
        //TODO 계산
        //userRepository.getMyUserId()
        //carRepository.getOwnerId()
        //carRepository.getNowRentUserId()

        return flowOf(UseState.OWNER)
    }
}