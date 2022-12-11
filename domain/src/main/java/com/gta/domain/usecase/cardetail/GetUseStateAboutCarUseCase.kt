package com.gta.domain.usecase.cardetail

import com.gta.domain.model.FirestoreException
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
    operator fun invoke(uid: String, carId: String): Flow<UCMCResult<UseState>> {
        return getNowRentCarUseCase(uid, carId).combine(carRepository.getCarRentState(carId)) { reservation, carRentState ->
            val ownerId = carRepository.getOwnerId(carId).first()
            if (reservation is UCMCResult.Success && carRentState is UCMCResult.Success && ownerId is UCMCResult.Success) {
                when {
                    // 내 아이디와 차주의 아이디가 같을 때 (first)
                    uid == ownerId.data -> {
                        UCMCResult.Success(UseState.OWNER)
                    }
                    // 차 아이디와 현재 대여중인 예약의 차 아이디가 같을 때 (실시간)
                    carId == reservation.data.carId -> {
                        UCMCResult.Success(UseState.NOW_RENT_USER)
                    }
                    // 현재 차 상태가 대여 불가능 일때 (실시간)
                    RentState.UNAVAILABLE == carRentState.data -> {
                        UCMCResult.Success(UseState.UNAVAILABLE)
                    }
                    else -> {
                        UCMCResult.Success(UseState.USER)
                    }
                }
            } else {
                UCMCResult.Error(FirestoreException())
            }
        }
    }
}
