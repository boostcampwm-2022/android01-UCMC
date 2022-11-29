package com.gta.domain.usecase

import com.gta.domain.model.Notification
import com.gta.domain.repository.NotificationRepository
import javax.inject.Inject

class SendNotificationUseCase @Inject constructor(private val notificationRepository: NotificationRepository) {
    suspend operator fun invoke(notification: Notification, receiverId: String): Boolean = notificationRepository.sendNotification(notification, receiverId)
}
