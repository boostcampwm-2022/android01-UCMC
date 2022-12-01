package com.gta.domain.model

data class Notification(
    val type: String = "",
    val message: String = "",
    val reservationId: String = "",
    val carId: String = "",
    val fromId: String = ""
)

enum class NotificationType(val title: String, val msg: String) {
    REQUEST_RESERVATION(
        "예약 요청",
        "자동차 대여 예약 요청이 도착했습니다."
    ),
    ACCEPT_RESERVATION(
        "예약 수락",
        "자동차 대여 예약이 완료됐습니다."
    ),
    DECLINE_RESERVATION(
        "예약 거절",
        "자동차 대여 예약이 거절됐습니다."
    ),
    RETURN_CAR(
        "차량 반납",
        "대여자가 차를 반납했습니다."
    )
}
