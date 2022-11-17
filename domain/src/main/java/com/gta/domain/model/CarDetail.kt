package com.gta.domain.model

enum class CarState(val string: String) {
    AVAILABLE("대여 가능"), UNAVAILABLE("대여 불가능"), RENTED("대여중")
}

data class CarDetail(
    val id: String,
    val title: String,
    val state: CarState,
    val location: String,
    val carType: String,
    val price: Int,
    val comment: String,
    val images: List<String>,
    val owner: UserProfile
)
