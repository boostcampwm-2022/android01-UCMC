package com.gta.data.source

import com.google.firebase.firestore.FirebaseFirestore
import com.gta.domain.model.AvailableDate
import com.gta.domain.model.Reservation
import com.gta.domain.model.ReservationState
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

    fun getRentingStateReservations(uid: String): Flow<List<Reservation>> = callbackFlow {
        fireStore
            .collection("reservations")
            .whereEqualTo("lenderId", uid)
            .whereEqualTo("state", ReservationState.RENTING.string)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    it.result.map { snapshot ->
                        snapshot.toObject(Reservation::class.java)
                    }.also { result ->
                        trySend(result)
                    }
                } else {
                    trySend(emptyList())
                }
            }
        awaitClose()
    }

    fun getCarReservationDates(carId: String): Flow<List<AvailableDate>> = callbackFlow {
        fireStore.collection("reservations").whereEqualTo("carId", carId).get().addOnCompleteListener {
            if (it.isSuccessful) {
                it.result.map { snapshot ->
                    snapshot.toObject(Reservation::class.java).reservationDate
                }.also { result ->
                    trySend(result)
                }
            } else {
                trySend(emptyList())
            }
        }
        awaitClose()
    }
}
