package com.gta.domain.model

data class NotificationInfo(
    val id: String,
    val type: NotificationType,
    val reservationId: String,
    val fromId: String,
    var fromNickName: String,
    var carImage: String?,
    var licensePlate: String,
    var date: String
)
