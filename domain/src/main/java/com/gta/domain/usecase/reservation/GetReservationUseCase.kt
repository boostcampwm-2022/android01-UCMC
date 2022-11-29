package com.gta.domain.usecase.reservation

import com.gta.domain.model.Reservation
import com.gta.domain.repository.ReservationRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetReservationUseCase @Inject constructor(
    private val repository: ReservationRepository
) {
    suspend operator fun invoke(reservationId: String, carId: String): Reservation {
        return repository.getReservationInfo(reservationId, carId).first()
    }
}
