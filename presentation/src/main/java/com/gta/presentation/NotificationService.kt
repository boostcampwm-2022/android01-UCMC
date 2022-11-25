package com.gta.presentation

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber

class NotificationService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        // 서버 토큰 저장
        Timber.d(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {}
}