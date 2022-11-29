package com.gta.domain.repository

import com.gta.domain.model.DrivingLicense
import kotlinx.coroutines.flow.Flow
import java.nio.ByteBuffer

interface LicenseRepository {
    fun getLicenseFromImage(buffer: ByteBuffer): Flow<DrivingLicense?>
    fun getLicenseFromDatabase(uid: String): Flow<DrivingLicense?>
    fun setLicense(uid: String, license: DrivingLicense, uri: String): Flow<Boolean>
}
