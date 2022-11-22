package com.gta.data.source

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.gta.domain.model.Reservation
import javax.inject.Inject

class ReservationDataSource @Inject constructor(private val fireStore: FirebaseFirestore) {
    fun createReservation(reservation: Reservation, reservationId: String): Task<Void> {
        return fireStore
            .collection("reservations")
            .document(reservationId)
            .set(reservation)
    }

    // TODO ID 값이 없은 경우 예외 처리
    // TODO addSnapshotListener
    fun getReservation(reservationId: String): Task<DocumentSnapshot> =
        fireStore.collection("reservations").document(reservationId).get()
}
