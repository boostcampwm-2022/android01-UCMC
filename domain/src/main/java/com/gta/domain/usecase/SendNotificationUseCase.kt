package com.gta.domain.usecase

import com.gta.domain.model.Notification
import com.gta.domain.model.UCMCResult
import com.gta.domain.repository.NotificationRepository
import javax.inject.Inject

class SendNotificationUseCase @Inject constructor(private val notificationRepository: NotificationRepository) {
    suspend operator fun invoke(notification: Notification, receiverId: String): UCMCResult<Unit> {
        val saveResult = notificationRepository.saveNotification(notification, receiverId)
        return if (saveResult is UCMCResult.Success) {
            notificationRepository.sendNotification(notification, receiverId)
        } else {
            saveResult
        }
    }
}
