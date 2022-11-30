package com.gta.domain.model

data class SimpleReservation(
    val id: String = "",
    val reservationDate: AvailableDate = AvailableDate()
)
