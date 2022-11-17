package com.gta.domain.usecase.license

import com.gta.domain.model.DrivingLicense
import com.gta.domain.repository.LicenseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SetLicenseUseCase @Inject constructor(
    private val repository: LicenseRepository
) {
    operator fun invoke(uid: String, license: DrivingLicense): Flow<Boolean> =
        repository.setLicense(uid, license)
}
