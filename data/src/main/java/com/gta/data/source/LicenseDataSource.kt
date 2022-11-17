package com.gta.data.source

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.gta.domain.model.DrivingLicense
import javax.inject.Inject

class LicenseDataSource @Inject constructor(
    private val databaseReference: DatabaseReference
) {
    fun registerLicense(uid: String, license: DrivingLicense): Task<Void> =
        databaseReference.child("users").child(uid).child("license").setValue(license)
}
