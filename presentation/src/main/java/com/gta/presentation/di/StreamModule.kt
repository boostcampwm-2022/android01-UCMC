package com.gta.presentation.di

import android.content.Context
import android.os.Bundle
import androidx.navigation.NavDeepLinkBuilder
import com.gta.presentation.R
import com.gta.presentation.secret.STREAM_KEY
import com.gta.presentation.ui.MainActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.client.notifications.handler.NotificationConfig
import io.getstream.chat.android.client.notifications.handler.NotificationHandler
import io.getstream.chat.android.client.notifications.handler.NotificationHandlerFactory
import io.getstream.chat.android.offline.plugin.configuration.Config
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import io.getstream.chat.android.pushprovider.firebase.FirebasePushDeviceGenerator
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StreamModule {
    @Singleton
    @Provides
    fun provideNotificationHandler(@ApplicationContext context: Context): NotificationHandler =
        NotificationHandlerFactory.createNotificationHandler(
            context = context,
            newMessageIntent = { _, channelType, channelId ->
                val bundle = Bundle().apply {
                    putString("cid", "$channelType:$channelId")
                }
                val intents = NavDeepLinkBuilder(context)
                    .setComponentName(MainActivity::class.java)
                    .setGraph(R.navigation.nav_main)
                    .addDestination(R.id.chattingListFragment)
                    .addDestination(R.id.chattingFragment, bundle)
                    .createTaskStackBuilder().intents
                intents.last()
            }
        )

    @Singleton
    @Provides
    fun provideNotificationConfig(): NotificationConfig = NotificationConfig(
        pushDeviceGenerators = listOf(FirebasePushDeviceGenerator(providerName = "ucmc")),
        pushNotificationsEnabled = true
    )

    @Singleton
    @Provides
    fun provideOfflinePluginFactory(@ApplicationContext context: Context): StreamOfflinePluginFactory =
        StreamOfflinePluginFactory(
            config = Config(),
            appContext = context
        )

    @Singleton
    @Provides
    fun provideChatClient(
        @ApplicationContext context: Context,
        offlinePluginFactory: StreamOfflinePluginFactory,
        notificationConfig: NotificationConfig,
        notificationHandler: NotificationHandler
    ): ChatClient = ChatClient.Builder(STREAM_KEY, context)
        .withPlugin(offlinePluginFactory)
        .notifications(notificationConfig, notificationHandler)
        .logLevel(ChatLogLevel.ALL)
        .build()
}
