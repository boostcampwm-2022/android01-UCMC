package com.gta.data.di

import com.gta.data.repository.ReservationRepositoryImpl
import com.gta.data.source.ReservationDataSource
import com.gta.domain.repository.ReservationRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ReservationModule {
    @Provides
    @Singleton
    fun providesReservationRepository(reservationDataSource: ReservationDataSource): ReservationRepository {
        return ReservationRepositoryImpl(reservationDataSource)
    }
}