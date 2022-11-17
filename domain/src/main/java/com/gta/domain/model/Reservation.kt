package com.gta.domain.model

data class Reservation(
    val carId: String,
    val userId: String,
    val state: String,
    val reservationDate: AvailableDate,
    val price: Int
)