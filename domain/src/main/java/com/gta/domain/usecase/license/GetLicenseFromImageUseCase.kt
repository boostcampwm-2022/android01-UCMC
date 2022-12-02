package com.gta.domain.usecase.license

import com.gta.domain.model.DrivingLicense
import com.gta.domain.repository.LicenseRepository
import kotlinx.coroutines.flow.Flow
import java.nio.ByteBuffer
import javax.inject.Inject

class GetLicenseFromImageUseCase @Inject constructor(
    private val repository: LicenseRepository
) {
    operator fun invoke(buffer: ByteBuffer): Flow<DrivingLicense?> = repository.getLicenseFromImage(buffer)
}
