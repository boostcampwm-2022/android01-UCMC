package com.gta.data.di

import com.gta.data.repository.ReviewRepositoryImpl
import com.gta.data.source.CarDataSource
import com.gta.data.source.ReservationDataSource
import com.gta.data.source.ReviewDataSource
import com.gta.data.source.UserDataSource
import com.gta.domain.repository.ReviewRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ReviewModule {

    @Singleton
    @Provides
    fun provideReviewRepository(
        reviewDataSource: ReviewDataSource,
        carDataSource: CarDataSource,
        reservationDataSource: ReservationDataSource,
        userDataSource: UserDataSource
    ): ReviewRepository = ReviewRepositoryImpl(
        reviewDataSource,
        carDataSource,
        reservationDataSource,
        userDataSource
    )
}
