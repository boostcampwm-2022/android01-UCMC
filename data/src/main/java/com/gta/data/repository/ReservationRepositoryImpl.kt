package com.gta.data.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.gta.data.source.CarDataSource
import com.gta.data.source.ReservationDataSource
import com.gta.domain.model.Reservation
import com.gta.domain.repository.ReservationRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class ReservationRepositoryImpl @Inject constructor(
    private val reservationDataSource: ReservationDataSource,
    private val carDataSource: CarDataSource
) : ReservationRepository {
    override fun createReservation(reservation: Reservation): Flow<Boolean> = callbackFlow {
        /*
            1. 차 정보 가져오기
            2. 차 예약 리스트 뒤에 새로운 예약 ID 붙이고 업데이트
            3. 에약 리스트 추가
         */
        val reservationId = "${System.currentTimeMillis()}${reservation.carId}"

        carDataSource.getCar(reservation.carId).addOnSuccessListener { snapshot ->
            val updateReservations = getUpdatedReservations(snapshot, reservationId)
            carDataSource.updateCarReservations(reservation.carId, updateReservations).addOnSuccessListener {
                reservationDataSource.createReservation(reservation, reservationId).addOnCompleteListener {
                    trySend(it.isSuccessful)
                }
            }.addOnFailureListener {
                trySend(false)
            }
        }.addOnFailureListener {
            trySend(false)
        }
        awaitClose()
    }

    private fun getUpdatedReservations(snapshot: DocumentSnapshot, reservationId: String) =
        (snapshot["reservations"] as? List<*>)?.plus(reservationId) ?: listOf(reservationId)
}
