package com.gta.domain.repository

import com.gta.domain.model.Reservation
import kotlinx.coroutines.flow.Flow

interface ReservationRepository {
    fun createReservation(reservation: Reservation): Flow<String>
    fun getReservationInfo(reservationId: String, carId: String): Flow<Reservation>
    fun getReservationCar(reservationId: String, carId: String): Flow<String>
}
