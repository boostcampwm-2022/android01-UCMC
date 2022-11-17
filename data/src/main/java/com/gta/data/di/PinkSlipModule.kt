package com.gta.data.di

import com.google.firebase.database.DatabaseReference
import com.gta.data.repository.PinkSlipRepositoryImpl
import com.gta.data.source.PinkSlipDataSource
import com.gta.domain.repository.PinkSlipRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PinkSlipModule {
    @Singleton
    @Provides
    fun providePinkSlipRepository(dataSource: PinkSlipDataSource): PinkSlipRepository =
        PinkSlipRepositoryImpl(dataSource)

    @Singleton
    @Provides
    fun providePinkSlipDataSource(databaseReference: DatabaseReference): PinkSlipDataSource =
        PinkSlipDataSource(databaseReference)
}
