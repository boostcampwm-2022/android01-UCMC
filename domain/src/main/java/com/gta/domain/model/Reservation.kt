package com.gta.domain.model

enum class ReservationState(val string: String) {
    CANCEL("취소"), PENDING("보류중"), ACCEPT("허락")
}

data class Reservation(
    val carId: String,
    val userId: String,
    val state: String = ReservationState.PENDING.string,
    val reservationDate: AvailableDate,
    val price: Int
)