package com.gta.data.source

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.gta.domain.model.UserReview
import javax.inject.Inject

class ReviewDataSource @Inject constructor(
    private val fireStore: FirebaseFirestore
) {
    fun addReview(opponentId: String, reservationId: String, review: UserReview): Task<Void> =
        fireStore
            .collection("users")
            .document(opponentId)
            .collection("reviews")
            .document(reservationId)
            .set(review)

    fun updateTemperature(opponentId: String, temperature: Float) =
        fireStore
            .collection("users")
            .document(opponentId)
            .update("temperature", temperature)
}
