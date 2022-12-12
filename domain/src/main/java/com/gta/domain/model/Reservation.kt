package com.gta.domain.model

enum class ReservationState(val state: Int) {
    CANCEL(-1), PENDING(0), ACCEPT(1), RENTING(2), DONE(-2)
}

data class Reservation(
    val carId: String = "정보 없음",
    val lenderId: String = "정보 없음",
    val ownerId: String = "정보 없음",
    val state: Int = ReservationState.PENDING.state,
    val reservationDate: AvailableDate = AvailableDate(),
    val price: Long = 0,
    val insuranceOption: String = InsuranceOption.LOW.name
)

fun Reservation.toSimpleReservation(reservationId: String): SimpleReservation = SimpleReservation(
    reservationId = reservationId,
    carId = carId,
    reservationState = state,
    reservationDate = reservationDate
)
