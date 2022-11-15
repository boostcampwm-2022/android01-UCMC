package com.gta.presentation.model.carDetail

import com.gta.domain.model.CarDetail
import com.gta.domain.model.CarState

enum class PriceType {
    Time, Day
}

enum class BtnType {
    Owner, Rented, User, NONE
}

data class CarInfo(
    val id: String,
    val title: String,
    val state: CarState,
    val location: String,
    val carType: String,
    val priceType: PriceType,
    val price: Int,
    val comment: String,
    val images: List<String>
)

fun CarDetail.toCarInfo(): CarInfo = CarInfo(
    id = id,
    title = title,
    state = state,
    location = location,
    carType = carType,
    priceType = PriceType.Day,
    price = price,
    comment = comment,
    images = images
)
