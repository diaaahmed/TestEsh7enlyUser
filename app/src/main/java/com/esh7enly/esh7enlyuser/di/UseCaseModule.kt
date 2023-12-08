package com.esh7enly.esh7enlyuser.di

import com.esh7enly.domain.repo.ServicesRepo
import com.esh7enly.domain.usecase.AccountCreationUpdateUseCase
import com.esh7enly.domain.usecase.MainUseCase
import com.esh7enly.domain.usecase.GetServicesUseCase
import com.esh7enly.domain.usecase.GetTransactionsUseCase
import com.esh7enly.domain.usecase.LoginUseCase
import com.esh7enly.domain.usecase.ScheduleUseCase
import com.esh7enly.domain.usecase.UserDataUseCase
import com.esh7enly.domain.usecase.XPayUseCase

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideUseCase(repo:ServicesRepo):MainUseCase{
        return MainUseCase(repo)
    }

    @Provides
    fun provideLoginUseCase(repo:ServicesRepo):LoginUseCase{
        return LoginUseCase(repo)
    }

    @Provides
    fun provideGetServicesUseCase(repo:ServicesRepo):GetServicesUseCase{
        return GetServicesUseCase(repo)
    }

    @Provides
    fun provideGetTransactionsUseCase(repo:ServicesRepo):GetTransactionsUseCase{
        return GetTransactionsUseCase(repo)
    }

    @Provides
    fun provideScheduleUseCase(repo:ServicesRepo):ScheduleUseCase{
        return ScheduleUseCase(repo)
    }

    @Provides
    fun provideXPayUseCase(repo:ServicesRepo):XPayUseCase{
        return XPayUseCase(repo)
    }

    @Provides
    fun provideAccountCreationUpdateUseCase(repo:ServicesRepo):AccountCreationUpdateUseCase{
        return AccountCreationUpdateUseCase(repo)
    }

    @Provides
    fun provideUserDataUseCase(repo:ServicesRepo):UserDataUseCase{
        return UserDataUseCase(repo)
    }
}