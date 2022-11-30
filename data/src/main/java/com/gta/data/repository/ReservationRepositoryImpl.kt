package com.gta.data.repository

import com.gta.data.source.CarDataSource
import com.gta.data.source.ReservationDataSource
import com.gta.data.source.UserDataSource
import com.gta.domain.model.Reservation
import com.gta.domain.repository.ReservationRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ReservationRepositoryImpl @Inject constructor(
    private val reservationDataSource: ReservationDataSource,
    private val carDataSource: CarDataSource,
    private val userDataSource: UserDataSource
) : ReservationRepository {
    override fun createReservation(reservation: Reservation): Flow<String> = callbackFlow {
        val reservationId = "${System.currentTimeMillis()}${reservation.carId}"
        val hasCar = carDataSource.getCar(reservation.carId).first()
        val hasUser = userDataSource.getUser(reservation.userId).first()

        if (hasCar != null && hasUser != null) {
            val storeCarResult = reservationDataSource.createReservationInCar(reservation, reservationId).first()
            val storeUserResult = reservationDataSource.createReservationInUser(reservation, reservationId).first()
            if (storeCarResult && storeUserResult) {
                trySend(reservationId)
            } else {
                trySend("")
            }
        } else {
            trySend("")
        }
        awaitClose()
    }

    override fun getReservationInfo(reservationId: String, carId: String): Flow<Reservation> =
        callbackFlow {
            reservationDataSource.getReservation(reservationId, carId).first()?.let { reservation ->
                trySend(reservation)
            } ?: trySend(Reservation())
            awaitClose()
        }

    override fun getCarReservationIds(carId: String): Flow<List<String>> {
        return reservationDataSource.getCarReservationIds(carId)
    }
}
