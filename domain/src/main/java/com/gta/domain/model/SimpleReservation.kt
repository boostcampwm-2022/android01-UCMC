package com.gta.domain.model

data class SimpleReservation(
    val reservationId: String = "",
    val carId: String = "",
    val reservationState: Int = ReservationState.PENDING.state,
    val reservationDate: AvailableDate = AvailableDate()
)

fun SimpleReservation.toTransaction(simpleCar: SimpleCar): Transaction {
    return Transaction(
        reservationId = reservationId,
        reservationState = ReservationState.values().find { it.state == reservationState } ?: ReservationState.RENTING,
        reservationDate = reservationDate,
        carModel = simpleCar.model,
        thumbnailImg = simpleCar.image
    )
}
