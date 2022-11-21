package com.gta.domain.repository

import com.gta.domain.model.Reservation
import kotlinx.coroutines.flow.Flow

interface ReservationRepository {
    fun createReservation(reservation: Reservation): Flow<Boolean>
}
