package com.esh7enly.domain.usecase

import com.esh7enly.domain.repo.ServicesRepo

class UserDataUseCase(private val servicesRepo: ServicesRepo)
{
    suspend fun getUserWallet(token: String) = servicesRepo.getUserWallet(token)

    suspend fun getUserPoints(token: String) = servicesRepo.getUserPoints(token)

    suspend fun replaceUserPoints(token: String) = servicesRepo.replaceUserPoints(token)

    suspend fun getDeposits(token: String,page:Int) = servicesRepo.getDeposits(token,page)

}