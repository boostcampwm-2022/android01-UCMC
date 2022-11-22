package com.gta.data.source

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class MyPageDataSource @Inject constructor(
    private val fireStore: FirebaseFirestore
) {
    fun setThumbnail(uid: String, downloadUrl: String): Task<Void> =
        fireStore.collection("users").document(uid).update("icon", downloadUrl)
}
