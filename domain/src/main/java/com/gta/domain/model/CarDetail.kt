package com.gta.domain.model

enum class RentState(val string: String) {
    AVAILABLE("대여 가능"), UNAVAILABLE("대여 불가능"), RENTED("대여중")
}

data class CarDetail(
    val id: String = "정보 없음",
    val carType: String = "정보 없음",
    val model: String = "정보 없음",
    val year: Int = 0,
    val licensePlate: String = "정보 없음",
    val price: Int = 0,
    val location: String = "정보 없음",
    val rentState: RentState = RentState.UNAVAILABLE,
    val comment: String = "",
    val availableDate: AvailableDate = AvailableDate(),
    val images: List<String> = emptyList(),
    val owner: UserProfile = UserProfile(),
    val coordinate: Coordinate = Coordinate(),
)
