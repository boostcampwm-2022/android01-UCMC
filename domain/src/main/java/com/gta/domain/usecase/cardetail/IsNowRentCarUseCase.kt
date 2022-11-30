package com.gta.domain.usecase.cardetail

import com.gta.domain.repository.ReservationRepository
import com.gta.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class IsNowRentCarUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val reservationRepository: ReservationRepository
) {
    operator fun invoke(uid: String, carId: String): Flow<Boolean> {
        /*
            1. 나의 예약 목록을 가져옴
            2. 특정 차의 모든 예약 가져옴
            3. 나의 예약 목록 Id, 특정 차의 모든 예약 Id 둘다에 속하면 현재 예약 중
         */
        return userRepository.getNowReservation(uid).combine(reservationRepository.getCarReservationIds(carId)) { nowReservationIds, carReservationIds ->
            val filterList = carReservationIds.filter { nowReservationIds.contains(it) }
            filterList.isNotEmpty()
        }
    }
}
