package com.gta.data.source

import com.google.firebase.firestore.FirebaseFirestore
import com.gta.data.model.UserInfo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class LoginDataSource @Inject constructor(
    private val fireStore: FirebaseFirestore
) {
    fun createUser(uid: String, messageToken: String): Flow<UserInfo?> = callbackFlow {
        val userInfo = UserInfo(messageToken = messageToken)
        fireStore
            .collection("users")
            .document(uid)
            .set(userInfo)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    trySend(userInfo)
                } else {
                    trySend(null)
                }
            }
        awaitClose()
    }
}
