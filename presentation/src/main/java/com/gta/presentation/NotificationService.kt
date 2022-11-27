package com.gta.presentation

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.gta.domain.usecase.notification.SetMessageTokenUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationService : FirebaseMessagingService() {
    @Inject lateinit var setMessageTokenUseCase: SetMessageTokenUseCase

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        coroutineScope.launch {
            setMessageTokenUseCase(token).collectLatest {
                // 성공 여부
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {}
}