package com.gta.data.di

import com.gta.data.repository.CarRepositoryImpl
import com.gta.data.repository.UserRepositoryImpl
import com.gta.data.source.CarDataSource
import com.gta.domain.repository.CarRepository
import com.gta.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(): UserRepository {
        return UserRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideCarRepository(carDataSource: CarDataSource): CarRepository {
        return CarRepositoryImpl(carDataSource)
    }
}
