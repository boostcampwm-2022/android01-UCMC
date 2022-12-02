package com.gta.data.source

import com.google.firebase.firestore.FirebaseFirestore
import com.gta.data.model.NotificationMessage
import com.gta.data.service.CloudMessageService
import com.gta.domain.model.Notification
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import javax.inject.Inject

class NotificationDataSource @Inject constructor(
    private val cloudMessageService: CloudMessageService,
    private val fireStore: FirebaseFirestore
) {
    suspend fun sendNotification(notification: Notification, to: String): Boolean {
        return kotlin.runCatching {
            cloudMessageService.sendNotificationMessage(
                NotificationMessage(
                    to = to,
                    data = notification
                )
            )
        }.onSuccess {
            Timber.d(it.results.toString())
        }.onFailure {
            Timber.d(it.message)
        }.isSuccess
    }

    fun saveNotification(notification: Notification, userId: String, notificationId: String): Flow<Boolean> =
        callbackFlow {
            fireStore.document("users/$userId/notifications/$notificationId").set(notification)
                .addOnCompleteListener {
                    trySend(it.isSuccessful)
                }
            awaitClose()
        }
}
