package com.gta.domain.repository

import com.gta.domain.model.PinkSlip
import com.gta.domain.model.UCMCResult
import java.nio.ByteBuffer

interface PinkSlipRepository {
    fun getPinkSlip(buffer: ByteBuffer): PinkSlip
    suspend fun setPinkSlip(uid: String, pinkSlip: PinkSlip): UCMCResult<Unit>
}
