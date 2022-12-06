package com.gta.domain.repository

import androidx.paging.PagingData
import com.gta.domain.model.Notification
import com.gta.domain.model.NotificationInfo
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun sendNotification(notification: Notification, receiverId: String): Boolean
    suspend fun saveNotification(notification: Notification, userId: String): Boolean
    fun getNotificationInfoList(userId: String): Flow<PagingData<NotificationInfo>>
}
