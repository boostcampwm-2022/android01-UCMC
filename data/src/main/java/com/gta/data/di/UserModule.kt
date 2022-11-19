package com.gta.data.di

import com.google.firebase.firestore.FirebaseFirestore
import com.gta.data.source.UserDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserModule {
    @Singleton
    @Provides
    fun provideUserDataSource(fireStore: FirebaseFirestore): UserDataSource =
        UserDataSource(fireStore)
}
