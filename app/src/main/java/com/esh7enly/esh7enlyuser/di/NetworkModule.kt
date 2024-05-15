package com.esh7enly.esh7enlyuser.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager


import com.esh7enly.data.remote.ApiService
import com.esh7enly.data.remote.NotificationService
import com.esh7enly.data.sharedhelper.SharedHelper
import com.esh7enly.domain.LiveDataCallAdapterFactory
import com.esh7enly.esh7enlyuser.BuildConfig

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideOkhttp(
        @ApplicationContext context: Context,
        sharedHelper: SharedHelper
    ): OkHttpClient {

        val logging = HttpLoggingInterceptor()

        if (BuildConfig.DEBUG)
        {
            logging.level = HttpLoggingInterceptor.Level.BODY
        }
        else {
            logging.level = HttpLoggingInterceptor.Level.NONE
        }

        // Create the Collector
        val chuckerCollector = ChuckerCollector(
            context = context,
            // Toggles visibility of the notification
            showNotification = true,
            // Allows to customize the retention period of collected data
            retentionPeriod = RetentionManager.Period.ONE_HOUR
        )

        // Create the Interceptor
        val chuckerInterceptor = ChuckerInterceptor.Builder(context)
            // The previously created Collector
            .collector(chuckerCollector)
            // The max body content length in bytes, after this responses will be truncated.
            .maxContentLength(250_000L)
            .redactHeaders("Authorization", "Bearer")
            .alwaysReadResponseBody(true)
            .build()

        return OkHttpClient.Builder()
//            .addInterceptor(chuckerInterceptor)
            .addInterceptor(logging)
            .addNetworkInterceptor(RequestInterceptor())
            .connectTimeout(3, TimeUnit.MINUTES)
            .readTimeout(3, TimeUnit.MINUTES)
            .writeTimeout(3, TimeUnit.MINUTES)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    @Named("notification")
    fun provideRetrofit2(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL_NOTIFICATION)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideNotificationService(@Named("notification") retrofit: Retrofit): NotificationService {
        return retrofit.create(NotificationService::class.java)
    }

}