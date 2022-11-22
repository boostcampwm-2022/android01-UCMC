package com.gta.data.source

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class UserDataSource @Inject constructor(
    private val fireStore: FirebaseFirestore
) {
    fun getUser(uid: String): Task<DocumentSnapshot> =
        fireStore.collection("users").document(uid).get()

    fun removeCar(uid: String, newCars: List<String>): Task<Void> =
        fireStore.collection("users").document(uid).update(mapOf("myCars" to newCars))
}
