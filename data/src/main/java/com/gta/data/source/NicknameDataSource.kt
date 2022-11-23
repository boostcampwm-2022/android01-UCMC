package com.gta.data.source

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class NicknameDataSource @Inject constructor(
    private val fireStore: FirebaseFirestore
) {
    fun updateNickname(uid: String, nickname: String): Task<Void> =
        fireStore.collection("users").document(uid).update("nickname", nickname)
}
