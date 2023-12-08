package com.esh7enly.esh7enlyuser.di

import android.content.Context
import androidx.room.Room
import com.esh7enly.data.local.DatabaseRepo
import com.esh7enly.data.local.DatabaseRoom
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule
{
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DatabaseRoom {
        return Room.databaseBuilder(context,DatabaseRoom::class.java,"category_database")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideDatabaseRepo(databaseRoom: DatabaseRoom): DatabaseRepo {
        return DatabaseRepo(databaseRoom)
    }
}