package com.gta.data.source

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.gta.domain.model.DrivingLicense
import javax.inject.Inject

class LicenseDataSource @Inject constructor(
    private val fireStore: FirebaseFirestore
) {
    fun registerLicense(uid: String, license: DrivingLicense): Task<Void> =
        fireStore.collection("users").document(uid).update("license", license)
}
