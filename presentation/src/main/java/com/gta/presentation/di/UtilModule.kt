package com.gta.presentation.di

import android.content.Context
import com.gta.presentation.util.ImageUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UtilModule {

    @Singleton
    @Provides
    fun provideImageUtil(@ApplicationContext context: Context): ImageUtil =
        ImageUtil(context.contentResolver)
}
