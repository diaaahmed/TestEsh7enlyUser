package com.esh7enly.domain.repo

import com.esh7enly.domain.entity.TransactionApiResponse
import com.esh7enly.domain.entity.TransactionDetailsEntity
import kotlinx.coroutines.flow.Flow

interface TransactionsRepo
{
    suspend fun getTransactions(token: String,page:Int): TransactionApiResponse

    suspend fun getTransactionDetails(
        token: String,
        transactionId: String
    ): Flow<TransactionDetailsEntity>
}