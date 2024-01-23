package com.esh7enly.esh7enlyuser.di

import android.content.Context
import com.esh7enly.data.sharedhelper.SharedHelper
import com.esh7enly.esh7enlyuser.util.*
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
    fun provideConnectivity(@ApplicationContext context: Context): Connectivity = Connectivity(context)


    @Singleton
    @Provides
    fun provideSharedHelper(@ApplicationContext context: Context): SharedHelper = SharedHelper(context)

    @Singleton
    @Provides
    fun provideCryptoData():CryptoData = CryptoData()

}