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
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onNewToken(token: String) {
        scope.launch {
            setMessageTokenUseCase(token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                getString(R.string.default_notification_channel_id),
                getString(R.string.default_notification_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val intent = createPendingIntent(message)

        val builder =
            NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle(message.data["type"])
                .setContentText(message.data["message"])
                .setContentIntent(intent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

        notificationManager.notify(0, builder.build())
    }

    private fun createPendingIntent(message: RemoteMessage): PendingIntent {
        val type = message.data["type"]
        val arguments = Bundle()
        val destinationId: Int

        when (type) {
            "예약 요청" -> {
                arguments.putString("CAR_ID", message.data["carId"])
                arguments.putString("RESERVATION_ID", message.data["reservationId"])
                destinationId = R.id.reservationRequestFragment
            }
            "예약 수락" -> {
                destinationId = R.id.notificationFragment
            }
            "예약 거절" -> {
                destinationId = R.id.notificationFragment
            }
            "차량 반납" -> {
                destinationId = R.id.mapFragment // TODO returnFragment
            }
            else -> {
                destinationId = R.id.mapFragment
            }
        }

        return NavDeepLinkBuilder(this)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.nav_main)
            .setDestination(destinationId)
            .setArguments(arguments)
            .createPendingIntent()
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
