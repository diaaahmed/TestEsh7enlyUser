package com.esh7enly.esh7enlyuser.di

import android.content.Context
import com.esh7enly.data.remote.ApiService
import com.esh7enly.data.repo.ServiceRepoImplNewTest
import com.esh7enly.data.repo.ServicesRepoImpl
import com.esh7enly.data.repo.TransactionsRepo
import com.esh7enly.data.repo.UserRepo
import com.esh7enly.data.repo.XPayRepo
import com.esh7enly.domain.repo.ServicesRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepoModule
{
    @Provides
    fun provideRepo(apiService: ApiService):ServicesRepoImpl{
        return ServicesRepoImpl(apiService)
    }

    @Singleton
    @Provides
    fun provideNewServicesRepo(apiService: ApiService):ServicesRepo{
        return ServiceRepoImplNewTest(apiService)
    }


    @Provides
    fun provideTransactionsRepo(apiService: ApiService):TransactionsRepo{
        return TransactionsRepo(apiService)
    }

    @Provides
    fun provideUserRepo(apiService: ApiService,@ApplicationContext context:Context):UserRepo{
        return UserRepo(apiService,context)
    }

    @Provides
    fun provideXpayRepo(apiService: ApiService):XPayRepo{
        return XPayRepo(apiService)
    }

}