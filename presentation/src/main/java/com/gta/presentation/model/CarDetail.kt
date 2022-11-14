package com.gta.presentation.model

import com.gta.domain.model.CarDetail

enum class PriceType {
    Time, Day
}

data class CarDetail(
    val id: String,
    val title: String,
    val state: String,
    val location: String,
    val carType: String,
    val priceType: PriceType,
    val price: Int,
    val comment: String,
    val images: List<String>
)

fun CarDetail.toPresentation(): com.gta.presentation.model.CarDetail =
    com.gta.presentation.model.CarDetail(
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
