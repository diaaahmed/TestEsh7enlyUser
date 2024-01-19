package com.esh7enly.esh7enlyuser.di

import com.esh7enly.data.local.DatabaseRoom
import com.esh7enly.data.remote.ApiService
import com.esh7enly.data.remote.NotificationService
import com.esh7enly.data.repo.ServicesRepoImpl
import com.esh7enly.data.repo.TransactionsRepo
import com.esh7enly.data.repo.UserRepo
import com.esh7enly.data.repo.XPayRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepoModule
{
    @Provides
    fun provideRepo(apiService: ApiService,
                    notificationService: NotificationService,
    databaseRoom: DatabaseRoom):ServicesRepoImpl{
        return ServicesRepoImpl(apiService,notificationService,databaseRoom)
    }

    @Provides
    fun provideXPayRepo(apiService: ApiService):XPayRepo{
        return XPayRepo(apiService)
    }

    @Provides
    fun provideTransactionsRepo(apiService: ApiService):TransactionsRepo{
        return TransactionsRepo(apiService)
    }

    @Provides
    fun provideUserRepo(apiService: ApiService):UserRepo{
        return UserRepo(apiService)
    }

}