package com.gta.data.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.gta.data.secret.KAKAO_REST_API_KEY
import com.gta.data.service.AddressSearchService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    const val BASE_URL = "https://dapi.kakao.com"

    @Singleton
    @Provides
    fun provideOkHttpClient(interceptor: Interceptor) = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build()

    @Singleton
    @Provides
    fun provideInterceptor() = Interceptor { chain ->
        with(chain) {
            val request = request()
                .newBuilder()
                .addHeader("Authorization", KAKAO_REST_API_KEY)
                .build()
            proceed(request)
        }
    }

    @Singleton
    @Provides
    fun provideGson(): Gson = GsonBuilder()
        .setLenient()
        .create()

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .baseUrl(BASE_URL)
        .build()

    @Singleton
    @Provides
    fun provideAddressSearchService(retrofit: Retrofit): AddressSearchService =
        retrofit.create(AddressSearchService::class.java)
}
