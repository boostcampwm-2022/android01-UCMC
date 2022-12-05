package com.gta.domain.repository

import com.gta.domain.model.Reservation
import com.gta.domain.model.ReservationState
import com.gta.domain.model.UCMCResult
import kotlinx.coroutines.flow.Flow

interface ReservationRepository {
    fun createReservation(reservation: Reservation): Flow<String>
    fun getReservationInfo(reservationId: String): Flow<UCMCResult<Reservation>>
    fun getReservationCar(reservationId: String): Flow<String>
    suspend fun updateReservationState(reservationId: String, state: ReservationState): Boolean
}
