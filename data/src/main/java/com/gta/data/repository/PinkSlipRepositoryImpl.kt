package com.gta.data.repository

import com.gta.data.source.PinkSlipDataSource
import com.gta.domain.model.PinkSlip
import com.gta.domain.repository.PinkSlipRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import java.nio.ByteBuffer
import javax.inject.Inject

class PinkSlipRepositoryImpl @Inject constructor(
    private val dataSource: PinkSlipDataSource
) : PinkSlipRepository {

    override fun getPinkSlip(buffer: ByteBuffer): Flow<PinkSlip?> = callbackFlow {
        trySend(dataSource.matizOrPorsche())
        awaitClose()
    }

    override fun setPinkSlip(uid: String, pinkSlip: PinkSlip): Flow<Boolean> = callbackFlow {
        /*
            1. 자기 자동차 리스트 가져오기
            2. 리스트 뒤에 새로운 차의 ID 붙이고 업데이트
            3. 차 테이블에 새로운 차 추가
         */
        dataSource.getCars(uid).addOnSuccessListener { cars ->
            val newCars = cars.children.map { it.value }.plus(pinkSlip.informationNumber)
            dataSource.updateCars(uid, newCars).addOnSuccessListener {
                dataSource.createCar(uid, pinkSlip).addOnCompleteListener {
                    trySend(it.isSuccessful)
                }
            }.addOnFailureListener {
                Timber.tag("PinkSlip").i("실패2")
                trySend(false)
            }
        }.addOnFailureListener {
            Timber.tag("PinkSlip").i("실패1")
            trySend(false)
        }
        awaitClose()
    }
}
