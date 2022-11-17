package com.gta.domain.repository

import com.gta.domain.model.DrivingLicense
import kotlinx.coroutines.flow.Flow
import java.nio.ByteBuffer

interface LicenseRepository {
    fun getLicense(buffer: ByteBuffer): Flow<DrivingLicense?>
    fun setLicense(uid: String, license: DrivingLicense): Flow<Boolean>
}
