package com.gta.data.di

import com.gta.data.repository.MessageTokenRepositoryImpl
import com.gta.data.source.MessageTokenDataSource
import com.gta.domain.repository.MessageTokenRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MessageTokenModule {
    @Singleton
    @Provides
    fun provideMessageTokenRepository(messageTokenDataSource: MessageTokenDataSource): MessageTokenRepository =
        MessageTokenRepositoryImpl(messageTokenDataSource)
}
