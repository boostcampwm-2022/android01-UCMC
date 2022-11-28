package com.gta.domain.usecase.cardetail

import com.gta.domain.repository.ReservationRepository
import com.gta.domain.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetNowRentCarUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val reservationRepository: ReservationRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(uid: String, carId: String): Flow<String?> {
        return userRepository.getNowReservation(uid).flatMapLatest { reservation ->
            reservation?.let { reservationRepository.getReservationCar(it, carId) } ?: flowOf(null)
        }
    }
}
