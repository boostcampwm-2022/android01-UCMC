package com.gta.data.source

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.gta.domain.model.Reservation
import javax.inject.Inject

class ReservationDataSource @Inject constructor(private val fireStore: FirebaseFirestore) {
    fun createReservation(reservation: Reservation): Task<Void> {
        return fireStore
            .collection("reservations")
            .document(System.currentTimeMillis().toString())
            .set(reservation)
    }
}