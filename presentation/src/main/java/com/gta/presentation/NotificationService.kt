package com.gta.presentation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.gta.domain.usecase.notification.SetMessageTokenUseCase
import com.gta.presentation.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.chat.android.pushprovider.firebase.FirebaseMessagingDelegate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationService : FirebaseMessagingService() {
    @Inject
    lateinit var setMessageTokenUseCase: SetMessageTokenUseCase

    private val scope = CoroutineScope(Dispatchers.IO)

    private val notificationManager: NotificationManager by lazy {
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    getString(R.string.default_notification_channel_id),
                    getString(R.string.default_notification_name),
                    NotificationManager.IMPORTANCE_HIGH
                )
                createNotificationChannel(channel)
            }
        }
    }

    override fun onNewToken(token: String) {
        runCatching {
            FirebaseMessagingDelegate.registerFirebaseToken(token, getString(R.string.chatting_provider_name))
        }
        scope.launch {
            setMessageTokenUseCase(token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        runCatching {
            if (FirebaseMessagingDelegate.handleRemoteMessage(message).not()) {
                handleUCMCMessage(message)
            }
        }
    }

    private fun handleUCMCMessage(message: RemoteMessage) {
        val builder = NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
            .setSmallIcon(R.drawable.ic_logo)
            .setContentTitle(message.data["type"])
            .setContentText(message.data["message"])
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        notificationManager.notify(0, builder.build())
    }

    private fun createPendingIntent(message: RemoteMessage): PendingIntent {
        val type = message.data["type"]

        val deepLinkBuilder = NavDeepLinkBuilder(this)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.nav_main)

        when (type) {
            "예약 요청" -> {
                val arguments = Bundle().apply {
                    putString("CAR_ID", message.data["carId"])
                    putString("RESERVATION_ID", message.data["reservationId"])
                }
                deepLinkBuilder.apply {
                    setArguments(arguments)
                    addDestination(R.id.notificationFragment)
                    addDestination(R.id.reservationRequestFragment)
                }
            }
            "예약 수락" -> {
                deepLinkBuilder.apply {
                    addDestination(R.id.notificationFragment)
                }
            }
            "예약 거절" -> {
                deepLinkBuilder.apply {
                    addDestination(R.id.notificationFragment)
                }
            }
            "차량 반납" -> {
                deepLinkBuilder.apply {
                    addDestination(R.id.mapFragment) // todo return fragment
                }
            }
            else -> {
                deepLinkBuilder.apply {
                    addDestination(R.id.mapFragment)
                }
            }
        }

        return deepLinkBuilder.createPendingIntent()
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
