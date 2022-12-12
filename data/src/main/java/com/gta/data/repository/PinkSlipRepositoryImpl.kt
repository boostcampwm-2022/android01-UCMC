package com.gta.data.repository

import com.gta.data.model.Car
import com.gta.data.source.CarDataSource
import com.gta.data.source.PinkSlipDataSource
import com.gta.data.source.UserDataSource
import com.gta.domain.model.DuplicatedItemException
import com.gta.domain.model.FirestoreException
import com.gta.domain.model.PinkSlip
import com.gta.domain.model.UCMCResult
import com.gta.domain.repository.PinkSlipRepository
import kotlinx.coroutines.flow.first
import java.nio.ByteBuffer
import javax.inject.Inject

class PinkSlipRepositoryImpl @Inject constructor(
    private val carDataSource: CarDataSource,
    private val userDataSource: UserDataSource,
    private val pinkSlipDataSource: PinkSlipDataSource
) : PinkSlipRepository {

    override fun getPinkSlip(buffer: ByteBuffer): PinkSlip =
        pinkSlipDataSource.getRandomPinkSlip()

    override suspend fun setPinkSlip(uid: String, pinkSlip: PinkSlip): UCMCResult<Unit> {
        /*
            1. 유저 정보 가져오기
            2. 리스트 뒤에 새로운 차의 ID 붙이고 업데이트
            3. 차 테이블에 새로운 차 추가
         */
        return userDataSource.getUser(uid).first()?.let { userInfo ->
            if (userInfo.myCars.contains(pinkSlip.informationNumber)) {
                UCMCResult.Error(DuplicatedItemException())
            } else {
                val updatedCars = userInfo.myCars.plus(pinkSlip.informationNumber)
                updateCars(uid, updatedCars, pinkSlip)
            }
        } ?: UCMCResult.Error(FirestoreException())
    }

    private suspend fun updateCars(uid: String, cars: List<String>, pinkSlip: PinkSlip): UCMCResult<Unit> {
        val car = Car(ownerId = uid, pinkSlip = pinkSlip)
        val result = pinkSlipDataSource.updateCars(uid, cars).first() && carDataSource.createCar(pinkSlip.informationNumber, car).first()
        return if (result) {
            UCMCResult.Success(Unit)
        } else {
            UCMCResult.Error(FirestoreException())
        }
    }
}
