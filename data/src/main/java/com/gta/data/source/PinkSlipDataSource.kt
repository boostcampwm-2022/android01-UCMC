package com.gta.data.source

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.gta.domain.model.Car
import com.gta.domain.model.PinkSlip
import javax.inject.Inject
import kotlin.random.Random

class PinkSlipDataSource @Inject constructor(
    private val databaseReference: DatabaseReference
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

    fun matizOrPorsche(): PinkSlip = cars
        .random(Random(System.currentTimeMillis()))
        .copy(informationNumber = getRandomInformationNumber())

    fun getCars(uid: String): Task<DataSnapshot> =
        databaseReference.child("users").child(uid).child("cars").get()

    fun updateCars(uid: String, cars: List<Any?>): Task<Void> =
        databaseReference.child("users").child(uid).child("cars").setValue(cars)

    fun createCar(uid: String, pinkSlip: PinkSlip): Task<Void> =
        databaseReference
            .child("cars")
            .child(pinkSlip.informationNumber)
            .setValue(Car(ownerId = uid, pinkSlip = pinkSlip))

    companion object {
        private const val INFORMATION_NUMBER_LENGTH = 17
    }
}
