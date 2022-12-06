package com.gta.domain.usecase.pinkslip

import com.gta.domain.model.PinkSlip
import com.gta.domain.model.UCMCResult
import com.gta.domain.repository.PinkSlipRepository
import javax.inject.Inject

class SetPinkSlipUseCase @Inject constructor(
    private val repository: PinkSlipRepository
) {
    suspend operator fun invoke(uid: String, pinkSlip: PinkSlip): UCMCResult<Unit> =
        repository.setPinkSlip(uid, pinkSlip)
}
