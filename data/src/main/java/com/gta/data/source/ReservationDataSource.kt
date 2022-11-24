package com.gta.data.source

import com.google.firebase.firestore.FirebaseFirestore
import com.gta.data.model.Car
import com.gta.domain.model.AvailableDate
import com.gta.domain.model.Reservation
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class ReservationDataSource @Inject constructor(private val fireStore: FirebaseFirestore) {
    fun createReservation(reservation: Reservation, reservationId: String): Flow<Boolean> = callbackFlow {
        fireStore
            .collection("reservations")
            .document(reservationId)
            .set(reservation)
            .addOnCompleteListener {
                trySend(it.isSuccessful)
            }
        awaitClose()
    }

    // TODO addSnapshotListener
    fun getReservation(reservationId: String): Flow<Reservation?> = callbackFlow {
        fireStore.collection("reservations").document(reservationId).get().addOnCompleteListener {
            if (it.isSuccessful) {
                trySend(it.result.toObject(Reservation::class.java))
            } else {
                trySend(null)
            }
        }
        awaitClose()
    }

    fun getAllReservations(): Flow<List<Reservation>> = callbackFlow {
        fireStore.collection("reservations").get().addOnCompleteListener {
            if (it.isSuccessful) {
                trySend(it.result.map { snapshot -> snapshot.toObject(Reservation::class.java) })
            } else {
                trySend(emptyList())
            }
        }
        awaitClose()
    }

    fun getCarReservationDates(car: Car): Flow<List<AvailableDate>> = callbackFlow {
        fireStore.collection("reservations").get().addOnCompleteListener {
            if (it.isSuccessful) {
                it.result.filter { document ->
                    car.reservations.contains(document.id)
                }.map { snapshot ->
                    snapshot.toObject(Reservation::class.java).reservationDate
                }
            } else {
                trySend(emptyList())
            }
        }
        awaitClose()
    }
}
