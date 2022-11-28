package com.gta.data.repository

import com.gta.data.source.CarDataSource
import com.gta.data.source.ReservationDataSource
import com.gta.domain.model.Reservation
import com.gta.domain.repository.ReservationRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ReservationRepositoryImpl @Inject constructor(
    private val reservationDataSource: ReservationDataSource,
    private val carDataSource: CarDataSource
) : ReservationRepository {
    override fun createReservation(reservation: Reservation): Flow<String> = callbackFlow {
        val reservationId = "${System.currentTimeMillis()}${reservation.carId}"
        carDataSource.getCar(reservation.carId).first()?.let { car ->
            if (reservationDataSource.createReservation(reservation, reservationId).first()) {
                trySend(reservationId)
            } else {
                trySend("")
            }
        } ?: trySend("")
        awaitClose()
    }

    override fun getReservationInfo(reservationId: String, carId: String): Flow<Reservation> = callbackFlow {
        reservationDataSource.getReservation(reservationId, carId).first()?.let { reservation ->
            trySend(reservation)
        } ?: trySend(Reservation())
        awaitClose()
    }

    override fun getReservationCar(reservationId: String, carId: String): Flow<String> {
        return getReservationInfo(reservationId, carId).map { it.carId }
    }
}
