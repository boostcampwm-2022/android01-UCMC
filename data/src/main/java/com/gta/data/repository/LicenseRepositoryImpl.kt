package com.gta.data.repository

import com.gta.data.source.LicenseDataSource
import com.gta.domain.model.DrivingLicense
import com.gta.domain.repository.LicenseRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import java.nio.ByteBuffer
import javax.inject.Inject

class LicenseRepositoryImpl @Inject constructor(
    private val dataSource: LicenseDataSource
) : LicenseRepository {
    override fun getLicenseFromImage(buffer: ByteBuffer): Flow<DrivingLicense?> = callbackFlow {
        // 그냥 객체를 바로 리턴 받을 수 있지만
        // 나중에 ML Kit를 사용하면 Flow로 받아와야 하므로 Flow 타입으로 정했습니다.
        trySend(
            DrivingLicense(
                name = "이동훈",
                id = "11-22-333444-55",
                residentNumberFront = "999999",
                residentNumberBack = "111111",
                aptitudeTestDate = "2026/01/01",
                expireDate = "2026/12/31"
            )
        )
        awaitClose()
    }

    override fun getLicenseFromDatabase(uid: String): Flow<DrivingLicense?> = callbackFlow {
        trySend(dataSource.getLicense(uid).first())
        awaitClose()
    }

    override fun setLicense(uid: String, license: DrivingLicense): Flow<Boolean> = callbackFlow {
        trySend(dataSource.registerLicense(uid, license).first())
        awaitClose()
    }
}
