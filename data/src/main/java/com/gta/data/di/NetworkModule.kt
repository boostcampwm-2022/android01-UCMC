package com.gta.data.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.gta.data.secret.CLOUD_MESSAGE_SERVER_KEY
import com.gta.data.secret.KAKAO_REST_API_KEY
import com.gta.data.service.AddressSearchService
import com.gta.data.service.CloudMessageService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "https://dapi.kakao.com"
    private const val CLOUD_MESSAGE_BASE_URL = "https://fcm.googleapis.com"

    @Qualifiers.SearchOkHttpClient
    @Singleton
    @Provides
    fun provideOkHttpClient(@Qualifiers.SearchInterceptor interceptor: Interceptor): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build()

    @Qualifiers.SearchInterceptor
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

    @Qualifiers.SearchRetrofit
    @Singleton
    @Provides
    fun provideRetrofit(@Qualifiers.SearchOkHttpClient okHttpClient: OkHttpClient, gson: Gson): Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .baseUrl(BASE_URL)
        .build()

    @Singleton
    @Provides
    fun provideAddressSearchService(@Qualifiers.SearchRetrofit retrofit: Retrofit): AddressSearchService =
        retrofit.create(AddressSearchService::class.java)

    @Qualifiers.CloudMessageOkHttpClient
    @Singleton
    @Provides
    fun provideCloudMessageOkHttpClient(@Qualifiers.CloudMessageInterceptor interceptor: Interceptor): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Qualifiers.CloudMessageInterceptor
    @Singleton
    @Provides
    fun provideCloudMessageInterceptor() = Interceptor { chain ->
        with(chain) {
            val request = request()
                .newBuilder()
                .addHeader("Authorization", "key=$CLOUD_MESSAGE_SERVER_KEY")
                .build()
            proceed(request)
        }
    }

    @Qualifiers.CloudMessageRetrofit
    @Singleton
    @Provides
    fun provideCloudMessageRetrofit(@Qualifiers.CloudMessageOkHttpClient okHttpClient: OkHttpClient, gson: Gson): Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .baseUrl(CLOUD_MESSAGE_BASE_URL)
        .build()

    @Singleton
    @Provides
    fun provideCloudMessageService(@Qualifiers.CloudMessageRetrofit retrofit: Retrofit): CloudMessageService =
        retrofit.create(CloudMessageService::class.java)
}
