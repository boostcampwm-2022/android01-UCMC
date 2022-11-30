package com.gta.domain.usecase.returncar

import com.gta.domain.model.SimpleReservation
import com.gta.domain.repository.ReservationRepository
import com.gta.domain.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetNowSimpleReservationUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val reservationRepository: ReservationRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(uid: String, carId: String): Flow<SimpleReservation?> {
        return userRepository.getNowReservation(uid).flatMapLatest { reservationId ->
            if (reservationId == null) {
                flowOf(null)
            } else {
                reservationRepository.getReservationDate(reservationId, carId).map { reservationDate ->
                    SimpleReservation(reservationId, reservationDate)
                }
            }
        }
    }
}
