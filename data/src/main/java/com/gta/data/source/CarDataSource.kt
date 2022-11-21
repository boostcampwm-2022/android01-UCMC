package com.gta.data.source

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import javax.inject.Inject

class CarDataSource @Inject constructor(
    private val fireStore: FirebaseFirestore
){

    fun getCar(carId: String): Task<DocumentSnapshot> {
        return fireStore.collection("cars").document(carId).get()
    }
}
