package com.gta.data.source

import com.google.firebase.database.DatabaseReference
import com.gta.domain.model.PinkSlip
import javax.inject.Inject
import kotlin.random.Random

class PinkSlipDataSource @Inject constructor(
    private val databaseReference: DatabaseReference
) {
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

    fun matizOrPorsche() = cars.random(Random(System.currentTimeMillis()))
}
