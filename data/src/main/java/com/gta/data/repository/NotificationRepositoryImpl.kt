package com.gta.data.repository

import com.gta.data.source.NotificationDataSource
import com.gta.data.source.UserDataSource
import com.gta.domain.model.Notification
import com.gta.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(private val notificationDataSource: NotificationDataSource, private val userDataSource: UserDataSource) : NotificationRepository {
    override suspend fun sendNotification(notification: Notification, receiverId: String): Boolean {
        val receiverToken = userDataSource.getUser(receiverId).first()?.messageToken ?: ""
        // 지금 아무도 토큰 없으므로 일단 내 것
        return notificationDataSource.sendNotification(notification, "d6GyQS2RQ5ygxIVIs9LqtH:APA91bF0THp5mqmYs6G-IYZE5O0FP2a6e7Cz-C7KLo7gZcNsNvf158uyK1QYsuf23qMQCRqVrfJtFTWteTPO6c9LNEraayb7QRIYZMykrJFGI2bxBi8nqz9B62fmLj5tvDOVG1GnDW5x")
    }
}
