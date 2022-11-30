package com.gta.domain.model

data class Notification(
    val type: String = "",
    val message: String = "",
    val reservationId: String = "",
    val carId: String = "",
    val fromId: String = ""
)

enum class NotificationType(val title: String) {
    REQUEST_RESERVATION("예약 요청"), ACCEPT_RESERVATION("예약 수락"), DECLINE_RESERVATION("예약 거절"), RETURN_CAR("차량 반납")
}
