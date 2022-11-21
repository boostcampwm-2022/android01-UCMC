package com.gta.data.di

import com.google.firebase.firestore.FirebaseFirestore
import com.gta.data.repository.CarRepositoryImpl
import com.gta.data.repository.UserRepositoryImpl
import com.gta.data.source.CarDataSource
import com.gta.data.source.UserDataSource
import com.gta.domain.repository.CarRepository
import com.gta.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(fireStore: FirebaseFirestore): UserRepository {
        return UserRepositoryImpl(UserDataSource(fireStore))
    }

    @Provides
    @Singleton
    fun provideCarRepository(fireStore: FirebaseFirestore): CarRepository {
        return CarRepositoryImpl(UserDataSource(fireStore), CarDataSource(fireStore))
    }
}
