package com.gta.domain.model

data class Transaction(
    val reservationId: String = "",
    val reservationState: ReservationState = ReservationState.PENDING,
    val reservationDate: AvailableDate = AvailableDate(),
    val carModel: String = "",
    val thumbnailImg: String = "",
)
