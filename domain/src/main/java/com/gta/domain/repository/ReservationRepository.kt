package com.gta.domain.repository

import com.gta.domain.model.AvailableDate
import com.gta.domain.model.Reservation
import kotlinx.coroutines.flow.Flow

interface ReservationRepository {
    fun createReservation(reservation: Reservation): Flow<String>
    fun getReservationInfo(reservationId: String): Flow<Reservation>
    fun getReservationCar(reservationId: String): Flow<String>
    fun getReservationDate(reservationId: String, carId: String): Flow<AvailableDate>
}
