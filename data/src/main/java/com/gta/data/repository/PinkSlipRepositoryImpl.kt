package com.gta.data.repository

import com.gta.data.source.PinkSlipDataSource
import com.gta.domain.model.PinkSlip
import com.gta.domain.repository.PinkSlipRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.nio.ByteBuffer
import javax.inject.Inject

class PinkSlipRepositoryImpl @Inject constructor(
    private val dataSource: PinkSlipDataSource
) : PinkSlipRepository {

    override fun getPinkSlip(buffer: ByteBuffer): Flow<PinkSlip?> = callbackFlow {
        // 그냥 객체를 바로 리턴 받을 수 있지만
        // 나중에 ML Kit를 사용하면 Flow로 받아와야 하므로 Flow 타입으로 정했습니다.
        trySend(dataSource.matizOrPorsche())
        awaitClose()
    }

    override fun setPinkSlip(uid: String, pinkSlip: PinkSlip): Flow<Boolean> = callbackFlow {
        trySend(true)
        awaitClose()
    }
}
