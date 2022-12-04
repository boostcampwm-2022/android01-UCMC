package com.gta.data.source

import com.google.firebase.firestore.FirebaseFirestore
import com.gta.data.model.UserInfo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class UserDataSource @Inject constructor(
    private val fireStore: FirebaseFirestore
) {
    fun getUser(uid: String): Flow<UserInfo> = callbackFlow {
        fireStore.collection("users").document(uid).get().addOnCompleteListener {
            if (it.isSuccessful) {
                trySend(it.result.toObject(UserInfo::class.java) ?: UserInfo())
            } else {
                trySend(UserInfo())
            }
        }
        awaitClose()
    }

    fun removeCar(uid: String, newCars: List<String>): Flow<Boolean> = callbackFlow {
        fireStore.collection("users").document(uid).update("myCars", newCars).addOnCompleteListener {
            trySend(it.isSuccessful)
        }
        awaitClose()
    }

    fun updateUserMessageToken(uid: String, token: String): Flow<Boolean> = callbackFlow {
        fireStore.collection("users").document(uid).update("messageToken", token).addOnCompleteListener {
            trySend(it.isSuccessful)
        }
        awaitClose()
    }

    fun addReportCount(uid: String, count: Int): Flow<Boolean> = callbackFlow {
        fireStore.collection("users").document(uid).update("reportCount", count).addOnCompleteListener {
            trySend(it.isSuccessful)
        }
        awaitClose()
    }
}
