package com.gta.data.repository

import com.gta.data.source.ReservationDataSource
import com.gta.domain.model.Reservation
import com.gta.domain.repository.ReservationRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class ReservationRepositoryImpl @Inject constructor(private val reservationDataSource: ReservationDataSource): ReservationRepository {
    override fun createReservation(reservation: Reservation): Flow<Boolean> = callbackFlow {
        reservationDataSource.createReservation(reservation).addOnCompleteListener {
            trySend(it.isSuccessful)
        }
        awaitClose()
    }
}