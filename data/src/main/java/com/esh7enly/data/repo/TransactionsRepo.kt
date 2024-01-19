package com.esh7enly.data.repo

import com.esh7enly.data.remote.ApiService
import com.esh7enly.domain.entity.TransactionApiResponse
import com.esh7enly.domain.entity.TransactionDetailsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class TransactionsRepo(private val apiService: ApiService)
{
    suspend fun getTransactions(token: String,page:Int): TransactionApiResponse =
        apiService.getTransactions(token,page)

    suspend fun getTransactionDetails(
        token: String,
        transactionId: String
    ): Flow<TransactionDetailsEntity> = flow {
        val response = apiService.getTransactionDetails(token,transactionId)
        emit(response)
    }.flowOn(Dispatchers.IO)


}