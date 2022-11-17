package com.gta.domain.usecase.pinkslip

import com.gta.domain.model.PinkSlip
import com.gta.domain.repository.PinkSlipRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SetPinkSlipUseCase @Inject constructor(
    private val repository: PinkSlipRepository
) {
    operator fun invoke(uid: String, pinkSlip: PinkSlip): Flow<Boolean> =
        repository.setPinkSlip(uid, pinkSlip)
}
