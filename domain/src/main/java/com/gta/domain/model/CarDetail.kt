package com.gta.domain.model

enum class CarState(val string: String) {
    AVAILABLE("대여 가능"), UNAVAILABLE("대여 불가능"), RENTED("대여중")
}

data class CarDetail(
    val images: List<String> = emptyList(),
    val carType: String,
    val model: String,
    val year: Int,
    val licensePlate: String,
    val price: Int = 10000,
    val location: String = "동훈이 집",
    val coordinate: Coordinate = Coordinate(),
    val rentState: String = CarState.UNAVAILABLE.string,
    val comment: String = "차였어요",
    val availableDate: AvailableDate = AvailableDate(),
    val reservations: List<String> = emptyList(),
    val ownerId: String
)
