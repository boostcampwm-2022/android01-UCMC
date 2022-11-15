package com.gta.data.di

import com.google.firebase.database.DatabaseReference
import com.gta.data.repository.LoginRepositoryImpl
import com.gta.data.source.LoginDataSource
import com.gta.domain.repository.LoginRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LoginModule {

    @Singleton
    @Provides
    fun provideLoginDataSource(databaseReference: DatabaseReference) =
        LoginDataSource(databaseReference)

    @Singleton
    @Provides
    fun provideLoginRepository(dataSource: LoginDataSource): LoginRepository =
        LoginRepositoryImpl(dataSource)
}
