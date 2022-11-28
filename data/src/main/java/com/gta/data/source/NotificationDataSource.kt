package com.gta.data.source

import com.gta.data.model.NotificationMessage
import com.gta.data.service.CloudMessageService
import com.gta.domain.model.Notification
import timber.log.Timber
import javax.inject.Inject

class NotificationDataSource @Inject constructor(private val cloudMessageService: CloudMessageService) {
    suspend fun sendNotification(notification: Notification, to: String): Boolean {
        return kotlin.runCatching {
            cloudMessageService.sendNotificationMessage(NotificationMessage(to = to, data = notification))
        }.onSuccess {
            Timber.d(it.results.toString())
        }.onFailure {
            Timber.d(it.message)
        }.isSuccess
    }
}