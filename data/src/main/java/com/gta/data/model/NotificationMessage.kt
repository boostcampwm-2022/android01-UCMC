package com.gta.data.model

import com.google.gson.annotations.SerializedName
import com.gta.domain.model.Notification

data class NotificationMessage(
    @SerializedName("to") val to: String,
    @SerializedName("priority") val priority: String = "high",
    @SerializedName("data") val data: Notification
)