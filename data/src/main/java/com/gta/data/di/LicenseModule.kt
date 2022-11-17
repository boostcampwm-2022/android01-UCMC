package com.gta.data.di

import com.google.firebase.database.DatabaseReference
import com.gta.data.repository.LicenseRepositoryImpl
import com.gta.data.source.LicenseDataSource
import com.gta.domain.repository.LicenseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LicenseModule {
    @Singleton
    @Provides
    fun provideLicenseDataSource(databaseReference: DatabaseReference): LicenseDataSource =
        LicenseDataSource(databaseReference)

    @Singleton
    @Provides
    fun provideLicenseRepository(dataSource: LicenseDataSource): LicenseRepository =
        LicenseRepositoryImpl(dataSource)
}
