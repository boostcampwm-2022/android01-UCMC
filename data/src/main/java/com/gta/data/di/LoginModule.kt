package com.gta.data.di

import com.google.firebase.firestore.FirebaseFirestore
import com.gta.data.repository.LoginRepositoryImpl
import com.gta.data.source.LoginDataSource
import com.gta.data.source.MessageTokenDataSource
import com.gta.data.source.UserDataSource
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
    fun provideLoginDataSource(fireStore: FirebaseFirestore): LoginDataSource =
        LoginDataSource(fireStore)

    @Singleton
    @Provides
    fun provideLoginRepository(
        userDataSource: UserDataSource,
        loginDataSource: LoginDataSource,
        messageTokenDataSource: MessageTokenDataSource
    ): LoginRepository = LoginRepositoryImpl(userDataSource, loginDataSource, messageTokenDataSource)
}
