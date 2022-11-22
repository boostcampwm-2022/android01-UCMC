package com.gta.data.di

import com.google.firebase.firestore.FirebaseFirestore
import com.gta.data.repository.MyPageRepositoryImpl
import com.gta.data.source.MyPageDataSource
import com.gta.data.source.StorageDataSource
import com.gta.domain.repository.MyPageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MyPageModule {

    @Singleton
    @Provides
    fun provideMyPageRepository(
        myPageDataSource: MyPageDataSource,
        storageDataSource: StorageDataSource
    ): MyPageRepository = MyPageRepositoryImpl(myPageDataSource, storageDataSource)

    @Singleton
    @Provides
    fun provideMyPageDataSource(
        fireStore: FirebaseFirestore
    ): MyPageDataSource = MyPageDataSource(fireStore)
}
