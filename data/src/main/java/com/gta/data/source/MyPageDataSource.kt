package com.gta.data.source

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class MyPageDataSource @Inject constructor(
    private val fireStore: FirebaseFirestore
) {
    fun setThumbnail(uid: String, downloadUrl: String): Flow<Boolean> = callbackFlow {
        fireStore.collection("users").document(uid).update("icon", downloadUrl).addOnCompleteListener {
            trySend(it.isSuccessful)
        }
        awaitClose()
    }
}
