package com.gta.data.source

import com.google.firebase.firestore.FirebaseFirestore
import com.gta.domain.model.AvailableDate
import com.gta.domain.model.Reservation
import com.gta.domain.model.ReservationState
import com.gta.domain.model.SimpleReservation
import com.gta.domain.model.toSimpleReservation
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

    fun getRentingStateReservations(uid: String): Flow<List<SimpleReservation>> = callbackFlow {
        fireStore
            .collection("reservations")
            .whereEqualTo("lenderId", uid)
            .whereEqualTo("state", ReservationState.RENTING.state)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    it.result.map { snapshot ->
                        snapshot.toObject(Reservation::class.java).toSimpleReservation(snapshot.id)
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
        fireStore.collection("reservations")
            .whereEqualTo("carId", carId)
            .whereGreaterThanOrEqualTo("state", ReservationState.PENDING.state)
            .get().addOnCompleteListener {
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

    fun updateReservationState(reservationId: String, state: Int) = callbackFlow {
        fireStore.document("reservations/$reservationId").update("state", state)
            .addOnCompleteListener {
                trySend(it.isSuccessful)
            }
        awaitClose()
    }
}
