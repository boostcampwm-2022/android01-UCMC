package com.gta.data.source

import com.google.firebase.firestore.FirebaseFirestore
import com.gta.domain.model.DrivingLicense
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class LicenseDataSource @Inject constructor(
    private val fireStore: FirebaseFirestore
) {
    fun registerLicense(uid: String, license: DrivingLicense): Flow<Boolean> = callbackFlow {
        fireStore.collection("users").document(uid).update("license", license)
            .addOnCompleteListener {
                trySend(it.isSuccessful)
            }
        awaitClose()
    }
}
