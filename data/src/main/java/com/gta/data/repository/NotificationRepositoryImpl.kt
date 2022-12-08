package com.gta.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.gta.data.source.CarDataSource
import com.gta.data.source.NotificationDataSource
import com.gta.data.source.NotificationPagingSource
import com.gta.data.source.ReservationDataSource
import com.gta.data.source.UserDataSource
import com.gta.domain.model.Notification
import com.gta.domain.model.NotificationInfo
import com.gta.domain.repository.NotificationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val notificationDataSource: NotificationDataSource,
    private val reservationDataSource: ReservationDataSource,
    private val userDataSource: UserDataSource,
    private val carDataSource: CarDataSource
) : NotificationRepository {
    override suspend fun sendNotification(notification: Notification, receiverId: String): Boolean {
        val user = userDataSource.getUser(receiverId).first() ?: return false
        val receiverToken = user.messageToken
        return notificationDataSource.sendNotification(notification, receiverToken)
    }

    override suspend fun saveNotification(notification: Notification, userId: String): Boolean {
        val notificationId = "${System.currentTimeMillis()}-$userId"
        return notificationDataSource.saveNotification(notification, userId, notificationId).first()
    }

    private val dateFormat = SimpleDateFormat("yy/MM/dd", Locale.getDefault())

    suspend fun getNotificationInfoDetailItem(notifyInfo: NotificationInfo): NotificationInfo {
        val reservation = reservationDataSource.getReservation(notifyInfo.reservationId).first()
        val from = notifyInfo.fromId
        val car = reservation?.carId ?: "정보 없음"

        withContext(Dispatchers.IO) {
            launch {
                userDataSource.getSuspendUser(from)?.let {
                    notifyInfo.fromNickName = it.nickname
                }
            }
            launch {
                carDataSource.getSuspendCar(car)?.let {
                    notifyInfo.licensePlate = it.pinkSlip.id
                    notifyInfo.carImage = if (it.images.isNotEmpty()) it.images[0] else null
                }
            }
        }

        notifyInfo.date = dateFormat.format(notifyInfo.date.toLong())

        return notifyInfo
    }

    override fun getNotificationInfoList(userId: String): Flow<PagingData<NotificationInfo>> {
        return Pager(PagingConfig(10)) {
            NotificationPagingSource(userId, notificationDataSource, this)
        }.flow
    }
}
