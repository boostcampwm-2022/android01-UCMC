package com.gta.domain.usecase.license

import com.gta.domain.model.DrivingLicense
import com.gta.domain.model.UCMCResult
import com.gta.domain.repository.LicenseRepository
import javax.inject.Inject

class SetLicenseUseCase @Inject constructor(
    private val repository: LicenseRepository
) {
    suspend operator fun invoke(uid: String, license: DrivingLicense, uri: String): UCMCResult<Unit> =
        repository.setLicense(uid, license, uri)
}
