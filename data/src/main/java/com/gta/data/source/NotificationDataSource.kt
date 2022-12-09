package com.gta.data.source

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.gta.data.model.NotificationMessage
import com.gta.data.service.CloudMessageService
import com.gta.domain.model.Notification
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
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
        }.isSuccess
    }

    fun saveNotification(
        notification: Notification,
        userId: String,
        notificationId: String
    ): Flow<Boolean> =
        callbackFlow {
            fireStore.document("users/$userId/notifications/$notificationId").set(notification)
                .addOnCompleteListener {
                    trySend(it.isSuccessful)
                }
            awaitClose()
        }

    private val ITEMS_PER_PAGE = 10L

    suspend fun getNotificationInfoCurrentItem(userId: String): QuerySnapshot {
        return fireStore.collection("users/$userId/notifications")
            .limit(ITEMS_PER_PAGE)
            .get()
            .await()
    }

    suspend fun getNotificationInfoNextItem(userId: String, doc: DocumentSnapshot): QuerySnapshot {
        return fireStore.collection("users/$userId/notifications")
            .limit(ITEMS_PER_PAGE)
            .startAfter(doc)
            .get()
            .await()
    }
}
