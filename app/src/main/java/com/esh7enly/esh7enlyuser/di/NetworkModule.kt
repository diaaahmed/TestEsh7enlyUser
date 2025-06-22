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
import com.esh7enly.esh7enlyuser.LoggingInterceptorLevel
import com.esh7enly.esh7enlyuser.connectivity.InternetAvailabilityRepository
import com.esh7enly.esh7enlyuser.connectivity.NoInternetInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.CertificatePinner
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private external fun baseUrl(): String

    init {
        System.loadLibrary("esh7enlyuser")
    }

    @Provides
    @Named("Header-Interceptor")
    fun provideHeaderInterceptor(
        sharedHelper: SharedHelper
    ): Interceptor {

        return HeaderInterceptor(sharedHelper)
    }

    @Named("Logging-interceptor")
    @Provides
    @Singleton
    fun providerLoggingInterceptor(): Interceptor {

        val loggingInterceptor = HttpLoggingInterceptor()

        loggingInterceptor.level = LoggingInterceptorLevel.level

        if (!BuildConfig.DEBUG) {
            loggingInterceptor.redactHeader("Authorization")
        }

        return loggingInterceptor

    }

    @Provides
    @Singleton
    fun provideCheckerInterceptor(
        @ApplicationContext context: Context,

        ): ChuckerInterceptor {

        // Create the Collector
        val chuckerCollector = ChuckerCollector(
            context = context,
            // Toggles visibility of the notification
            showNotification = true,
            // Allows to customize the retention period of collected data
            retentionPeriod = RetentionManager.Period.ONE_HOUR
        )

        // Create the Interceptor
        return ChuckerInterceptor.Builder(context)
            // The previously created Collector
            .collector(chuckerCollector)
            // The max body content length in bytes, after this responses will be truncated.
            .maxContentLength(250_000L)
            .redactHeaders("Authorization", "Bearer")
            .alwaysReadResponseBody(true)
            .build()


    }

    private fun createCertificatePinning(): CertificatePinner {
        return CertificatePinner.Builder()
            .add("diaa.com", "sha256/OEuKLd1BnD0ailSGdeSgHT8GtHyJoyz6k4WczlaAdkM=")
            .build()
    }

    @Provides
    @Singleton
    fun provideOkhttp(
        @Named("Logging-interceptor") loggingInterceptor: Interceptor,
        @Named("Header-Interceptor") headerInterceptor: Interceptor,
        internetRepo: InternetAvailabilityRepository
    ): OkHttpClient {

        return OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .addInterceptor(loggingInterceptor)
            // .certificatePinner(createCertificatePinning())
            .addInterceptor(NoInternetInterceptor(internetRepo))
            .addInterceptor(headerInterceptor)
         //   .addSSLSocketFactory(context)
            //.addNetworkInterceptor(chuckerInterceptor)
            .connectTimeout(3, TimeUnit.MINUTES)
            .readTimeout(3, TimeUnit.MINUTES)
            .writeTimeout(3, TimeUnit.MINUTES)
            .build()
    }

    private fun OkHttpClient.Builder.addSSLSocketFactory(
        context: Context
    ) = apply {
        val certificateFactory = CertificateFactory.getInstance("X.509")

        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
            load(null, null)
        }

        context.assets.open("ca_bundles.txt").use { inputStream ->
            // Split the bundle into individual certificates based on the PEM delimiters
            val certificates = inputStream.bufferedReader().use { it.readText() }
                .split("-----END CERTIFICATE-----")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .map { it + "\n-----END CERTIFICATE-----" } // Re-add the end line for parsing

            certificates.forEachIndexed { index, certificatePEM ->
                val certInputStream = certificatePEM.byteInputStream()
                try {
                    val certificate = certificateFactory.generateCertificate(certInputStream)
                    val alias = "ca_cert_$index"
                    keyStore.setCertificateEntry(alias, certificate)
                } catch (e: Exception) {
                    // Handle parsing error, log or skip
                    println("Error parsing certificate at index $index: ${e.message}")
                }
            }
        }
        // Initialize TrustManager with the loaded certificates
        val trustManagerFactory = TrustManagerFactory.getInstance(
            TrustManagerFactory.getDefaultAlgorithm()
        ).apply {
            init(keyStore)
        }
        val trustManager = trustManagerFactory.trustManagers[0] as X509TrustManager

        // Create an SSLContext with the TrustManager
        val sslContext = SSLContext.getInstance("TLSv1.3").apply {
            init(null, arrayOf(trustManager), SecureRandom())
        }
        sslSocketFactory(sslContext.socketFactory, trustManager)
    }


    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl())
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