package com.gta.domain.repository

import com.gta.domain.model.PinkSlip
import kotlinx.coroutines.flow.Flow
import java.nio.ByteBuffer

interface PinkSlipRepository {
    fun getPinkSlip(buffer: ByteBuffer): Flow<PinkSlip?>
    fun setPinkSlip(uid: String, pinkSlip: PinkSlip): Flow<Boolean>
}
