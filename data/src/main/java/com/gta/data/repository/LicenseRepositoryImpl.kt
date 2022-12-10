package com.gta.data.repository

import android.content.res.Resources.NotFoundException
import com.gta.data.source.LicenseDataSource
import com.gta.data.source.StorageDataSource
import com.gta.data.source.UserDataSource
import com.gta.domain.model.DrivingLicense
import com.gta.domain.model.ExpiredItemException
import com.gta.domain.model.FirestoreException
import com.gta.domain.model.UCMCResult
import com.gta.domain.repository.LicenseRepository
import kotlinx.coroutines.flow.first
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class LicenseRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource,
    private val licenseDataSource: LicenseDataSource,
    private val storageDataSource: StorageDataSource
) : LicenseRepository {

    private val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

    override fun getLicenseFromImage(buffer: ByteBuffer): DrivingLicense =
        DrivingLicense(
            name = "이동훈",
            id = "11-22-333444-55",
            residentNumberFront = "999999",
            residentNumberBack = "111111",
            aptitudeTestDate = "2026/01/01",
            expireDate = "2026/12/31"
        )

    override suspend fun getLicenseFromDatabase(uid: String): UCMCResult<DrivingLicense> =
        userDataSource.getUser(uid).first()?.let { user ->
            if (user.license == null) {
                UCMCResult.Error(NotFoundException())
            } else {
                UCMCResult.Success(user.license)
            }
        } ?: UCMCResult.Error(FirestoreException())


    override suspend fun setLicense(uid: String, license: DrivingLicense, uri: String): UCMCResult<Unit> {
        val expireDate = dateFormat.parse(license.expireDate)?.time ?: 0L
        // 만료시간을 초과했는지 검사
        if (expireDate < System.currentTimeMillis()) {
            return UCMCResult.Error(ExpiredItemException())
        }
        val uploadResult = storageDataSource.uploadPicture("users/$uid/license", uri).first() ?: ""
        return if (uploadResult.isNotEmpty()) {
            val registerResult = licenseDataSource.registerLicense(uid, license.copy(uri = uploadResult)).first()
            if (registerResult) {
                UCMCResult.Success(Unit)
            } else {
                UCMCResult.Error(FirestoreException())
            }
        } else {
            UCMCResult.Error(FirestoreException())
        }
    }
}
