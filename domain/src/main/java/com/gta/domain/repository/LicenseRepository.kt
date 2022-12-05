package com.gta.domain.repository

import com.gta.domain.model.DrivingLicense
import com.gta.domain.model.UCMCResult
import java.nio.ByteBuffer

interface LicenseRepository {
    fun getLicenseFromImage(buffer: ByteBuffer): DrivingLicense
    suspend fun getLicenseFromDatabase(uid: String): UCMCResult<DrivingLicense>
    suspend fun setLicense(uid: String, license: DrivingLicense, uri: String): UCMCResult<Unit>
}
