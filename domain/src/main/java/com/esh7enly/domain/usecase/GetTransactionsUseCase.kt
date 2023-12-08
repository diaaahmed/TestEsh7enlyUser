package com.esh7enly.domain.usecase

import com.esh7enly.domain.repo.ServicesRepo

class GetTransactionsUseCase(private val servicesRepo: ServicesRepo)
{
    suspend fun getTransactions(token: String,page:Int) = servicesRepo.getTransactions(token,page)

    suspend fun getTransactionDetails(token: String,transactionId:String) = servicesRepo.getTransactionDetails(token,transactionId)
}