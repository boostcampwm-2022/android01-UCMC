package com.gta.data.repository

import com.gta.data.source.PinkSlipDataSource
import com.gta.data.source.UserDataSource
import com.gta.domain.model.PinkSlip
import com.gta.domain.repository.PinkSlipRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import java.nio.ByteBuffer
import javax.inject.Inject

class PinkSlipRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource,
    private val pinkSlipDataSource: PinkSlipDataSource
) : PinkSlipRepository {

    override fun getPinkSlip(buffer: ByteBuffer): Flow<PinkSlip?> = callbackFlow {
        trySend(pinkSlipDataSource.matizOrPorsche())
        awaitClose()
    }

    override fun setPinkSlip(uid: String, pinkSlip: PinkSlip): Flow<Boolean> = callbackFlow {
        /*
            1. 유저 정보 가져오기
            2. 리스트 뒤에 새로운 차의 ID 붙이고 업데이트
            3. 차 테이블에 새로운 차 추가
         */
        userDataSource.getUser(uid).first()?.let { userInfo ->
            val updatedCars = userInfo.myCars.plus(pinkSlip.informationNumber)
            pinkSlipDataSource.run {
                trySend(updateCars(uid, updatedCars).first() && createCar(uid, pinkSlip).first())
            }
        } ?: trySend(false)
        awaitClose()
    }
}
