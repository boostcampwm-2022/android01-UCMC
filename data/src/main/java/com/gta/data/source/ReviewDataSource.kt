package com.gta.data.source

import com.google.firebase.firestore.FirebaseFirestore
import com.gta.domain.model.UserReview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class ReviewDataSource @Inject constructor(
    private val fireStore: FirebaseFirestore
) {
    fun addReview(opponentId: String, reservationId: String, review: UserReview): Flow<Boolean> = callbackFlow {
        fireStore
            .collection("users")
            .document(opponentId)
            .collection("reviews")
            .document(reservationId)
            .set(review)
            .addOnCompleteListener {
                trySend(it.isSuccessful)
            }
        awaitClose()
    }

    fun updateTemperature(opponentId: String, temperature: Float): Flow<Boolean> = callbackFlow {
        fireStore
            .collection("users")
            .document(opponentId)
            .update("temperature", temperature)
            .addOnCompleteListener {
                trySend(it.isSuccessful)
            }
        awaitClose()
    }
}
