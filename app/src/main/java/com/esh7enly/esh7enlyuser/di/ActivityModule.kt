package com.esh7enly.esh7enlyuser.di

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.esh7enly.data.datastore.DataStoreHelper
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
    fun provideSharedHelper(@ApplicationContext context: Context): SharedHelper = SharedHelper(context)


    @Singleton
    @Provides
    fun provideDataStoreHelper(@ApplicationContext context: Context): DataStoreHelper = DataStoreHelper(context)

    @RequiresApi(Build.VERSION_CODES.M)
    @Singleton
    @Provides
    fun provideCryptoData(): CryptoData = CryptoData()

}