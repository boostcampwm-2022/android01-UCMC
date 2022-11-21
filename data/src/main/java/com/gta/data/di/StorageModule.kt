package com.gta.data.di

import com.google.firebase.storage.StorageReference
import com.gta.data.source.StorageDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Singleton
    @Provides
    fun provideStorageDataSource(storageReference: StorageReference): StorageDataSource =
        StorageDataSource(storageReference)
}
