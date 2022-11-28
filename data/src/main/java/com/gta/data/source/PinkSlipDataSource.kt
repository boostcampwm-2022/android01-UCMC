package com.gta.data.source

import com.google.firebase.firestore.FirebaseFirestore
import com.gta.data.model.Car
import com.gta.domain.model.PinkSlip
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import kotlin.random.Random

class PinkSlipDataSource @Inject constructor(
    private val fireStore: FirebaseFirestore
) {
    private val rand = Random(System.currentTimeMillis())

    private val charset = "ABCDEFGHIJKLMNOPQRSTUVWXTZ0123456789"

    private val cars = listOf(
        Triple("올 뉴 마티즈", "경형 승용", 2007),
        Triple("포르쉐 911", "대형 승용", 2022),
        Triple("BMW 730Ld xDrive", "대형 승용", 2018),
        Triple("마이바흐 62s", "대형 승용", 2008),
        Triple("페라리 458 Italy", "대형 승용", 2014),
        Triple("아반떼", "중형 승용", 2012),
        Triple("GV80", "대형 승용", 2022),
        Triple("싼타페", "중형 승용", 2013),
        Triple("그랜저", "중형 승용", 2020),
        Triple("벤틀리 플라잉스퍼", "대형 승용", 2020),
        Triple("C220d", "중형 승용", 2019),
        Triple("티코", "경형 승용", 1994),
        Triple("르망", "소형 승용", 1993),
        Triple("MINI Cooper S", "소형 승용", 2012)
    )

    private val carIds = listOf("가", "나", "다", "라", "마")

    fun updateCars(uid: String, cars: List<Any?>): Flow<Boolean> = callbackFlow {
        fireStore.collection("users").document(uid).update("myCars", cars).addOnCompleteListener {
            trySend(it.isSuccessful)
        }
        awaitClose()
    }

    fun createCar(uid: String, pinkSlip: PinkSlip): Flow<Boolean> = callbackFlow {
        fireStore
            .collection("cars")
            .document(pinkSlip.informationNumber)
            .set(Car(ownerId = uid, pinkSlip = pinkSlip))
            .addOnCompleteListener {
                trySend(it.isSuccessful)
            }
        awaitClose()
    }

    fun getRandomPinkSlip(): PinkSlip {
        val (model, type, year) = cars.random(rand)
        return PinkSlip(
            id = getRandomCarId(),
            informationNumber = getRandomInformationNumber(),
            owner = "이동훈",
            type = type,
            model = model,
            year = year
        )
    }

    private fun getRandomInformationNumber() =
        (1..INFORMATION_NUMBER_LENGTH).map { charset.random(rand) }.joinToString("")

    private fun getRandomCarId() =
        "${rand.nextInt(100, 699)}${carIds.random(rand)}${rand.nextInt(9999).toString().padStart(4, '0')}"

    companion object {
        private const val INFORMATION_NUMBER_LENGTH = 17
    }
}
