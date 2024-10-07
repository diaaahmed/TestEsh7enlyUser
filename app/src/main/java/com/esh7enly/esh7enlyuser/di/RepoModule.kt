package com.esh7enly.esh7enlyuser.di

import com.esh7enly.data.repo.ServiceRepoImpl
import com.esh7enly.data.repo.TransactionsRepoImpl
import com.esh7enly.data.repo.UserRepoImpl
import com.esh7enly.data.repo.ChargeBalanceRepoImpl
import com.esh7enly.domain.repo.ChargeBalanceRepo
import com.esh7enly.domain.repo.ServicesRepo
import com.esh7enly.domain.repo.TransactionsRepo
import com.esh7enly.domain.repo.UserRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepoModule {

    @Singleton
    @Binds
    abstract fun provideNewServicesRepo(serviceRepoImp: ServiceRepoImpl): ServicesRepo

    @Binds
    @Singleton
    abstract fun provideTransactionsRepo(transactionRepoImp: TransactionsRepoImpl): TransactionsRepo

    @Singleton
    @Binds
    abstract fun provideUserRepo(userRepoImp: UserRepoImpl): UserRepo

    @Binds
    @Singleton
    abstract fun provideChargeBalanceRepo(chargeBalanceRepoImp: ChargeBalanceRepoImpl): ChargeBalanceRepo

}