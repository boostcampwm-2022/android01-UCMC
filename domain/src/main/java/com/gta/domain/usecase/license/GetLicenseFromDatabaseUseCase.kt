package com.gta.domain.usecase.license

import com.gta.domain.model.DrivingLicense
import com.gta.domain.repository.LicenseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLicenseFromDatabaseUseCase @Inject constructor(
    private val repository: LicenseRepository
) {
    operator fun invoke(uid: String): Flow<DrivingLicense?> = repository.getLicenseFromDatabase(uid)
}
