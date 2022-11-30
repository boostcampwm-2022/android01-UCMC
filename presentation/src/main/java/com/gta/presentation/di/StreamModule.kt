package com.gta.presentation.di

import android.content.Context
import com.gta.presentation.secret.STREAM_KEY
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.client.notifications.handler.NotificationConfig
import io.getstream.chat.android.offline.plugin.configuration.Config
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import io.getstream.chat.android.pushprovider.firebase.FirebasePushDeviceGenerator
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StreamModule {

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
        notificationConfig: NotificationConfig
    ): ChatClient = ChatClient.Builder(STREAM_KEY, context)
        .withPlugin(offlinePluginFactory)
        .notifications(notificationConfig)
        .logLevel(ChatLogLevel.ALL)
        .build()
}
