package com.gta.data.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.gta.data.source.PinkSlipDataSource
import com.gta.data.source.UserDataSource
import com.gta.domain.model.PinkSlip
import com.gta.domain.repository.PinkSlipRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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
        userDataSource.getUser(uid).addOnSuccessListener { snapshot ->
            val updatedCars = getUpdatedCars(snapshot, pinkSlip.informationNumber)
            pinkSlipDataSource.updateCars(uid, updatedCars).addOnSuccessListener {
                pinkSlipDataSource.createCar(uid, pinkSlip).addOnCompleteListener {
                    trySend(it.isSuccessful)
                }
            }.addOnFailureListener {
                trySend(false)
            }
        }.addOnCompleteListener {
            trySend(false)
        }
        awaitClose()
    }

    private fun getUpdatedCars(snapshot: DocumentSnapshot, informationNumber: String) =
        (snapshot["myCars"] as? List<*>)?.plus(informationNumber) ?: listOf(informationNumber)
}
