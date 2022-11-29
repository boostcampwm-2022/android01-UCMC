package com.gta.data.repository

import com.gta.data.source.NotificationDataSource
import com.gta.data.source.UserDataSource
import com.gta.domain.model.Notification
import com.gta.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(private val notificationDataSource: NotificationDataSource, private val userDataSource: UserDataSource) : NotificationRepository {
    override suspend fun sendNotification(notification: Notification, receiverId: String): Boolean {
        val receiverToken = userDataSource.getUser(receiverId).first()?.messageToken ?: return false
        return notificationDataSource.sendNotification(notification, receiverToken)
    }
}
