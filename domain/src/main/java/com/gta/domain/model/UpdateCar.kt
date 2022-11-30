package com.gta.domain.model

data class UpdateCar(
    val images: List<String> = emptyList(),
    val price: Int,
    val comment: String = "",
    val rentState: RentState,
    val availableDate: AvailableDate,
    val location: String,
    val coordinate: Coordinate
)
