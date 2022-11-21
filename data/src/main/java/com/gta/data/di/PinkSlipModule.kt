package com.gta.data.di

import com.google.firebase.firestore.FirebaseFirestore
import com.gta.data.repository.PinkSlipRepositoryImpl
import com.gta.data.source.PinkSlipDataSource
import com.gta.data.source.UserDataSource
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
    fun providePinkSlipRepository(
        userDataSource: UserDataSource,
        pinkSlipDataSource: PinkSlipDataSource
    ): PinkSlipRepository = PinkSlipRepositoryImpl(userDataSource, pinkSlipDataSource)

    @Singleton
    @Provides
    fun providePinkSlipDataSource(fireStore: FirebaseFirestore): PinkSlipDataSource =
        PinkSlipDataSource(fireStore)
}
