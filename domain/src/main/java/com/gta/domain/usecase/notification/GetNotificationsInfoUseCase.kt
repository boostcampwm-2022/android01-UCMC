package com.gta.domain.usecase.notification

import androidx.paging.PagingData
import com.gta.domain.model.NotificationInfo
import com.gta.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetNotificationsInfoUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
){
    operator fun invoke(userId: String): Flow<PagingData<NotificationInfo>> {
        return notificationRepository.getNotificationInfoList(userId)
    }
}
