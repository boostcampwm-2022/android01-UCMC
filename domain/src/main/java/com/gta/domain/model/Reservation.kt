package com.gta.domain.model

enum class ReservationState(val string: String) {
    CANCEL("취소"), PENDING("보류중"), ACCEPT("허락"), RENTING("대여중"), DONE("반납완료")
}

data class Reservation(
    val carId: String = "정보 없음",
    val lenderId: String = "정보 없음",
    val ownerId: String = "정보 없음",
    val state: String = ReservationState.PENDING.string,
    val reservationDate: AvailableDate = AvailableDate(),
    val price: Int = 0,
    val insuranceOption: String = InsuranceOption.LOW.name
)
