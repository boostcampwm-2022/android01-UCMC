package com.gta.domain.model

enum class CarState(val string: String) {
    Available("대여 가능"), Unavailable("대여 불가능"), Rented("대여중")
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
