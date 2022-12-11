package com.gta.data.repository

import com.gta.data.source.CarDataSource
import com.gta.data.source.ReservationDataSource
import com.gta.domain.model.FirestoreException
import com.gta.domain.model.Reservation
import com.gta.domain.model.ReservationState
import com.gta.domain.model.UCMCResult
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
    override suspend fun createReservation(reservation: Reservation): UCMCResult<String> {
        val reservationId = "${System.currentTimeMillis()}${reservation.carId}"
        return carDataSource.getCar(reservation.carId).first()?.let {
            if (reservationDataSource.createReservation(reservation, reservationId).first()) {
                UCMCResult.Success(reservationId)
            } else {
                UCMCResult.Error(FirestoreException())
            }
        } ?: UCMCResult.Error(FirestoreException())
    }

    override fun getReservationInfo(reservationId: String): Flow<UCMCResult<Reservation>> =
        callbackFlow {
            reservationDataSource.getReservation(reservationId).first()?.let { reservation ->
                trySend(UCMCResult.Success(reservation))
            } ?: trySend(UCMCResult.Error(FirestoreException()))
            awaitClose()
        }

    override fun getReservationCar(reservationId: String): Flow<String> {
        return getReservationInfo(reservationId).map {
            if (it is UCMCResult.Success) {
                it.data.carId
            } else {
                ""
            }
        }
    }

    override suspend fun updateReservationState(
        reservationId: String,
        state: ReservationState
    ): UCMCResult<Unit> {
        return if (reservationDataSource.updateReservationState(reservationId, state.state).first()) {
            UCMCResult.Success(Unit)
        } else {
            UCMCResult.Error(FirestoreException())
        }
    }
}
