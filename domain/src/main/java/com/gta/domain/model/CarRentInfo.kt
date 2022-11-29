package com.gta.domain.model

data class CarRentInfo(
    val images: List<String> = emptyList(),
    val model: String = "",
    val price: Int = 10000,
    val comment: String = "차였어요",
    val availableDate: AvailableDate = AvailableDate(),
    val reservationDates: List<AvailableDate> = emptyList(),
    val ownerId: String = ""
)
