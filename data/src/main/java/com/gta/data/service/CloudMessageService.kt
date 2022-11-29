package com.gta.data.service

import com.gta.data.model.MessageResult
import com.gta.data.model.NotificationMessage
import retrofit2.http.Body
import retrofit2.http.POST

interface CloudMessageService {
    @POST("fcm/send")
    suspend fun sendNotificationMessage(
        @Body message: NotificationMessage
    ): MessageResult
}
