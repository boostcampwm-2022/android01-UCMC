package com.gta.domain.model

data class SimpleCar(
    val id: String = "정보 없음",
    val image: String = "",
    val carType: String = "정보 없음",
    val model: String = "정보 없음",
    val year: Int = 0,
    val price: Int = 0,
    val coordinate: Coordinate = Coordinate()
)
