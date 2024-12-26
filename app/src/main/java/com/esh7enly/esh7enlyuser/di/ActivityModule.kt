package com.esh7enly.esh7enlyuser.di

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.security.crypto.EncryptedSharedPreferences
import com.esh7enly.data.datastore.DataStoreHelper
import com.esh7enly.data.sharedhelper.Constants
import com.esh7enly.data.sharedhelper.SharedHelper
import com.esh7enly.esh7enlyuser.util.Connectivity
import com.esh7enly.esh7enlyuser.util.CryptoData
import com.esh7enly.esh7enlyuser.util.Decryptor
import com.esh7enly.esh7enlyuser.util.DynamicLayout
import com.esh7enly.esh7enlyuser.util.Encryptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ActivityModule
{
    @Singleton
    @Provides
    fun provideDynamicLayout(@ApplicationContext context: Context): DynamicLayout = DynamicLayout(context)

    @Singleton
    @Provides
    fun providerDecryptor() = Decryptor()

    @Singleton
    @Provides
    fun provideEncryptor():Encryptor = Encryptor()



    @Singleton
    @Provides
    fun provideConnectivity(@ApplicationContext context: Context): Connectivity = Connectivity(context)


    @Singleton
    @Provides
    fun provideSharedHelper(
        @ApplicationContext context: Context,
        @Named(Constants.ENCRYPTED_SHARED_PREF) sharedPreferences: SharedPreferences
    ): SharedHelper = SharedHelper(context,sharedPreferences)

    @Singleton
    @Provides
    @Named(Constants.ENCRYPTED_SHARED_PREF)
    fun provideEncryptedSharedPreferences(@ApplicationContext context: Context): SharedPreferences  {
        return EncryptedSharedPreferences.create(
            Constants.SHARED_PREF_FILE_NAME,
            "shared_setting",
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
    }


    @Singleton
    @Provides
    fun provideDataStoreHelper(
        @ApplicationContext context: Context,
        cryptoData: CryptoData): DataStoreHelper = DataStoreHelper(context,cryptoData)


    @RequiresApi(Build.VERSION_CODES.M)
    @Singleton
    @Provides
    fun provideCryptoData(): CryptoData = CryptoData()

}