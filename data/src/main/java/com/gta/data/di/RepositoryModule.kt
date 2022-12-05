package com.gta.data.di

import android.content.Context
import com.gta.data.repository.CarRepositoryImpl
import com.gta.data.repository.MapRepositoryImpl
import com.gta.data.repository.ReportRepositoryImpl
import com.gta.data.repository.UserRepositoryImpl
import com.gta.data.source.CarDataSource
import com.gta.data.source.MapDataSource
import com.gta.data.source.ReservationDataSource
import com.gta.data.source.StorageDataSource
import com.gta.data.source.UserDataSource
import com.gta.domain.repository.CarRepository
import com.gta.domain.repository.MapRepository
import com.gta.domain.repository.ReportRepository
import com.gta.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(
        userDataSource: UserDataSource,
        reservationDataSource: ReservationDataSource
    ): UserRepository {
        return UserRepositoryImpl(userDataSource, reservationDataSource)
    }

    @Provides
    @Singleton
    fun provideCarRepository(
        userDataSource: UserDataSource,
        carDataSource: CarDataSource,
        reservationDataSource: ReservationDataSource,
        storageDataSource: StorageDataSource
    ): CarRepository {
        return CarRepositoryImpl(
            userDataSource,
            carDataSource,
            reservationDataSource,
            storageDataSource
        )
    }

    @Provides
    @Singleton
    fun provideMapRepository(mapDataSource: MapDataSource): MapRepository {
        return MapRepositoryImpl(mapDataSource)
    }

    @Provides
    @Singleton
    fun provideReportRepository(
        @ApplicationContext context: Context,
        userDataSource: UserDataSource
    ): ReportRepository =
        ReportRepositoryImpl(context, userDataSource)
}
