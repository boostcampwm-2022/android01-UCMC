package com.gta.domain.model

enum class ReservationState(val string: String) {
    CANCEL("취소"), PENDING("보류중"), ACCEPT("허락"), RENTING("대여중"), DONE("반납완료")
}

data class Reservation(
    val carId: String = "",
    val lenderId: String = "",
    val ownerId: String = "",
    val state: String = ReservationState.PENDING.string,
    val reservationDate: AvailableDate = AvailableDate(),
    val price: Int = 0,
    val insuranceOption: String = InsuranceOption.LOW.name
)

fun Reservation.toSimpleReservation(reservationId: String): SimpleReservation = SimpleReservation(
    reservationId = reservationId,
    carId = carId,
    reservationDate = reservationDate
)