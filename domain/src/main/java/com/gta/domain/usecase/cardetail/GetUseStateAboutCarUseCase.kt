package com.gta.domain.usecase.cardetail

import com.gta.domain.model.RentState
import com.gta.domain.model.UCMCResult
import com.gta.domain.repository.CarRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import javax.inject.Inject

enum class UseState {
    OWNER, NOW_RENT_USER, USER, UNAVAILABLE
}

class GetUseStateAboutCarUseCase @Inject constructor(
    private val carRepository: CarRepository,
    private val getNowRentCarUseCase: GetNowRentCarUseCase
) {
    operator fun invoke(uid: String, carId: String): Flow<UseState> {
        return getNowRentCarUseCase(uid, carId).combine(carRepository.getCarRentState(carId)) { reservation, carRentState ->
            val ownerIdResult = carRepository.getOwnerId(carId).first()
            when {
                ownerIdResult is UCMCResult.Success && uid == ownerIdResult.data -> {
                    UseState.OWNER
                }
                carId == reservation.carId -> {
                    UseState.NOW_RENT_USER
                }
                RentState.UNAVAILABLE == carRentState -> {
                    UseState.UNAVAILABLE
                }
                else -> {
                    UseState.USER
                }
            }
        }
    }
}
