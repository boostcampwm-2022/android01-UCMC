package com.gta.data.di

import com.gta.data.repository.ReviewRepositoryImpl
import com.gta.data.source.ReviewDataSource
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
    fun provideReviewRepository(dataSource: ReviewDataSource): ReviewRepository =
        ReviewRepositoryImpl(dataSource)
}
