package com.gta.data.di

import com.google.firebase.firestore.FirebaseFirestore
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
    fun provideLicenseDataSource(fireStore: FirebaseFirestore): LicenseDataSource =
        LicenseDataSource(fireStore)

    @Singleton
    @Provides
    fun provideLicenseRepository(dataSource: LicenseDataSource): LicenseRepository =
        LicenseRepositoryImpl(dataSource)
}
