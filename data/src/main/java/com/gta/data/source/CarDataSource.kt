package com.gta.data.source

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class CarDataSource @Inject constructor(
    private val fireStore: FirebaseFirestore
) {
    fun getCar(uid: String): Task<DocumentSnapshot> =
        fireStore.collection("cars").document(uid).get()
}