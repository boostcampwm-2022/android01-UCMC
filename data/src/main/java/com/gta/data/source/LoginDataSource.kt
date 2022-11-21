package com.gta.data.source

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.gta.data.model.UserInfo
import javax.inject.Inject

class LoginDataSource @Inject constructor(
    private val fireStore: FirebaseFirestore
) {
    fun createUser(uid: String): Task<Void> =
        fireStore
            .collection("users")
            .document(uid)
            .set(UserInfo(chatId = uid.hashCode().toLong()))
}
