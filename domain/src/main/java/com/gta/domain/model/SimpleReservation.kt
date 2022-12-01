package com.gta.domain.model

data class SimpleReservation(
    val reservationId: String = "",
    val carId: String = "",
    val reservationDate: AvailableDate = AvailableDate()
)
