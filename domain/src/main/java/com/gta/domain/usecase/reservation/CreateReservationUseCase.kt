package com.gta.domain.usecase.reservation

import com.gta.domain.model.Reservation
import com.gta.domain.repository.ReservationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateReservationUseCase @Inject constructor(private val repository: ReservationRepository) {
    operator fun invoke(reservation: Reservation): Flow<Boolean> {
        return repository.createReservation(reservation)
    }
}