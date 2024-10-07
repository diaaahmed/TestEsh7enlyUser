package com.esh7enly.esh7enlyuser.di

import com.esh7enly.data.remote.ApiService
import com.esh7enly.data.repo.ChargeBalanceRepoImpl
import com.esh7enly.data.repo.ServiceRepoImpl
import com.esh7enly.data.repo.TransactionsRepoImpl
import com.esh7enly.data.repo.UserRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class RepoImplModule {

    @Provides
    fun provideRepo(apiService: ApiService): ServiceRepoImpl {
        return ServiceRepoImpl(apiService)
    }

    @Provides
    fun provideUserRepoImpl(apiService: ApiService): UserRepoImpl {
        return UserRepoImpl(apiService)
    }

    @Provides
    fun provideTransactionsRepoImpl(apiService: ApiService): TransactionsRepoImpl {
        return TransactionsRepoImpl(apiService)
    }

    @Provides
    fun provideChargeBalanceRepoImpl(apiService: ApiService): ChargeBalanceRepoImpl {
        return ChargeBalanceRepoImpl(apiService)
    }
}