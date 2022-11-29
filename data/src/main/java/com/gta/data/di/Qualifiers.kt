package com.gta.data.di

import javax.inject.Qualifier

object Qualifiers {
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
}
