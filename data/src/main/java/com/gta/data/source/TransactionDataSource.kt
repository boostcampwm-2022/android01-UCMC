package com.gta.data.source

import com.google.firebase.firestore.FirebaseFirestore
import com.gta.domain.model.Reservation
import com.gta.domain.model.SimpleReservation
import com.gta.domain.model.toSimpleReservation
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class TransactionDataSource @Inject constructor(private val fireStore: FirebaseFirestore) {

    fun getYourCarTransactions(uid: String): Flow<List<SimpleReservation>> = callbackFlow {
        fireStore.collection("reservations").whereEqualTo("lenderId", uid).get().addOnCompleteListener {
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

    fun getMyCarTransactions(uid: String): Flow<List<SimpleReservation>> = callbackFlow {
        fireStore.collection("reservations").whereEqualTo("ownerId", uid).get().addOnCompleteListener {
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
}
