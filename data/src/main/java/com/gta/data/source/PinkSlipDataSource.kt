package com.gta.data.source

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.gta.data.model.Car
import com.gta.domain.model.PinkSlip
import javax.inject.Inject
import kotlin.random.Random

class PinkSlipDataSource @Inject constructor(
    private val fireStore: FirebaseFirestore
) {
    private val charset = "ABCDEFGHIJKLMNOPQRSTUVWXTZ0123456789"

    private val cars = listOf(
        PinkSlip(
            id = "12서 5678",
            informationNumber = "ABCDEF12345W12345",
            owner = "이동훈",
            type = "경형 승용",
            model = "올 뉴 마티즈",
            year = 2007
        ),
        PinkSlip(
            id = "123서 5678",
            informationNumber = "ABCDEF12345W12345",
            owner = "이동훈",
            type = "대형 승용",
            model = "포르쉐 911",
            year = 2022
        )
    )

    private fun getRandomInformationNumber(): String {
        val rand = Random(System.currentTimeMillis())
        return (1..INFORMATION_NUMBER_LENGTH).map { charset.random(rand) }.joinToString("")
    }

    // 가짜 자동차 등록증 데이터를 반환 합니다.
    fun matizOrPorsche(): PinkSlip = cars
        .random(Random(System.currentTimeMillis()))
        .copy(informationNumber = getRandomInformationNumber())

    fun updateCars(uid: String, cars: List<Any?>): Task<Void> =
        fireStore.collection("users").document(uid).update("myCars", cars)

    fun createCar(uid: String, pinkSlip: PinkSlip): Task<Void> =
        fireStore
            .collection("cars")
            .document(pinkSlip.informationNumber)
            .set(Car(ownerId = uid, pinkSlip = pinkSlip))

    companion object {
        private const val INFORMATION_NUMBER_LENGTH = 17
    }
}
