package com.gta.data.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.gta.data.secret.CLOUD_MESSAGE_SERVER_KEY
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
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "https://dapi.kakao.com"
    private const val CLOUD_MESSAGE_BASE_URL = "https://fcm.googleapis.com"

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class SearchRetrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class SearchOkHttpClient

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class SearchInterceptor

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class CloudMessageRetrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class CloudMessageOkHttpClient

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class CloudMessageInterceptor

    @SearchOkHttpClient
    @Singleton
    @Provides
    fun provideOkHttpClient(@SearchInterceptor interceptor: Interceptor): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build()

    @SearchInterceptor
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

    @SearchRetrofit
    @Singleton
    @Provides
    fun provideRetrofit(@SearchOkHttpClient okHttpClient: OkHttpClient, gson: Gson): Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .baseUrl(BASE_URL)
        .build()

    @Singleton
    @Provides
    fun provideAddressSearchService(@SearchRetrofit retrofit: Retrofit): AddressSearchService =
        retrofit.create(AddressSearchService::class.java)

    @CloudMessageOkHttpClient
    @Singleton
    @Provides
    fun provideCloudMessageOkHttpClient(@CloudMessageInterceptor interceptor: Interceptor): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build()

    @CloudMessageInterceptor
    @Singleton
    @Provides
    fun provideCloudMessageInterceptor() = Interceptor { chain ->
        with(chain) {
            val request = request()
                .newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", CLOUD_MESSAGE_SERVER_KEY)
                .build()
            proceed(request)
        }
    }

    @CloudMessageRetrofit
    @Singleton
    @Provides
    fun provideCloudMessageRetrofit(@CloudMessageOkHttpClient okHttpClient: OkHttpClient, gson: Gson): Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .baseUrl(CLOUD_MESSAGE_BASE_URL)
        .build()
}
