package com.gta.data.di

import com.gta.data.repository.NotificationRepositoryImpl
import com.gta.data.source.NotificationDataSource
import com.gta.data.source.UserDataSource
import com.gta.domain.repository.NotificationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {
    @Singleton
    @Provides
    fun provideNotificationRepository(notificationDataSource: NotificationDataSource, userDataSource: UserDataSource): NotificationRepository =
        NotificationRepositoryImpl(notificationDataSource, userDataSource)
}