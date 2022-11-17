package com.gta.domain.usecase.pinkslip

import com.gta.domain.model.PinkSlip
import com.gta.domain.repository.PinkSlipRepository
import kotlinx.coroutines.flow.Flow
import java.nio.ByteBuffer
import javax.inject.Inject

class GetPinkSlipUseCase @Inject constructor(
    private val repository: PinkSlipRepository
) {
    operator fun invoke(buffer: ByteBuffer): Flow<PinkSlip?> = repository.getPinkSlip(buffer)
}
