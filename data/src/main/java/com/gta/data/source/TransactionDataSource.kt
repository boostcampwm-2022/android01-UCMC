package com.gta.data.source

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TransactionDataSource @Inject constructor(private val fireStore: FirebaseFirestore) {
    private val pagingSize = 10L

    suspend fun getYourCarTransactions(uid: String): QuerySnapshot {
        return fireStore.collection("reservations")
            .whereEqualTo("lenderId", uid)
            .limit(pagingSize)
            .get()
            .await()
    }

    suspend fun getYourCarTransactionsFromCursor(
        uid: String,
        docCursor: DocumentSnapshot
    ): QuerySnapshot {
        return fireStore.collection("reservations")
            .whereEqualTo("lenderId", uid)
            .limit(pagingSize)
            .startAfter(docCursor)
            .get()
            .await()
    }

    suspend fun getMyCarTransactions(uid: String): QuerySnapshot {
        return fireStore.collection("reservations")
            .whereEqualTo("ownerId", uid)
            .limit(pagingSize)
            .get()
            .await()
    }

    suspend fun getMyCarTransactionsFromCursor(
        uid: String,
        docCursor: DocumentSnapshot
    ): QuerySnapshot {
        return fireStore.collection("reservations")
            .whereEqualTo("ownerId", uid)
            .limit(pagingSize)
            .startAfter(docCursor)
            .get()
            .await()
    }
}
