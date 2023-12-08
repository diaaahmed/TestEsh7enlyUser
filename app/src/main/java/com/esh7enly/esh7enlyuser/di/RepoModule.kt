package com.esh7enly.esh7enlyuser.di


import com.esh7enly.data.local.DatabaseRoom
import com.esh7enly.data.remote.ApiService
import com.esh7enly.data.remote.NotificationService
import com.esh7enly.data.repo.ServicesRepoImpl
import com.esh7enly.domain.repo.ServicesRepo
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
    databaseRoom: DatabaseRoom):ServicesRepo{
        return ServicesRepoImpl(apiService,notificationService,databaseRoom)
    }

}