package com.gta.domain.repository

import androidx.paging.PagingData
import com.gta.domain.model.Notification
import com.gta.domain.model.NotificationInfo
import com.gta.domain.model.UCMCResult
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun sendNotification(notification: Notification, receiverId: String): UCMCResult<Unit>
    suspend fun saveNotification(notification: Notification, userId: String): UCMCResult<Unit>
    fun getNotificationInfoList(userId: String): Flow<PagingData<NotificationInfo>>
}
