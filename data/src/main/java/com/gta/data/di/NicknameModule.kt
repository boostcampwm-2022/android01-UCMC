package com.gta.data.di

import com.gta.data.repository.NicknameRepositoryImpl
import com.gta.data.source.NicknameDataSource
import com.gta.domain.repository.NicknameRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NicknameModule {

    @Singleton
    @Provides
    fun provideNicknameRepository(dataSource: NicknameDataSource): NicknameRepository =
        NicknameRepositoryImpl(dataSource)
}
