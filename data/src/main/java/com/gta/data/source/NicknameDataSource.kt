package com.gta.data.source

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class NicknameDataSource @Inject constructor(
    private val fireStore: FirebaseFirestore
) {
    fun updateNickname(uid: String, nickname: String): Flow<Boolean> = callbackFlow {
        fireStore.collection("users").document(uid).update("nickname", nickname).addOnCompleteListener {
            trySend(it.isSuccessful)
        }
        awaitClose()
    }
}
