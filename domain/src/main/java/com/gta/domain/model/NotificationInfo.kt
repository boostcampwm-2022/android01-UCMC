package com.gta.domain.model

data class NotificationInfo(
    val id: String,
    val type: NotificationType,
    val reservationId: String,
    val fromNickName: String,
    val carImage: String?,
    val licensePlate: String,
    val date: String
)
