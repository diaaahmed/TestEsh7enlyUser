package com.esh7enly.esh7enlyuser.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esh7enly.domain.NetworkResult
import com.esh7enly.domain.entity.TransactionApiResponse
import com.esh7enly.domain.entity.TransactionDetailsEntity
import com.esh7enly.domain.repo.TransactionsRepo
import com.esh7enly.esh7enlyuser.util.sendIssueToCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val transactionsRepo: TransactionsRepo
): ViewModel()
{
    private var _responseTransactions: MutableLiveData<NetworkResult<TransactionApiResponse>>
    = MutableLiveData()

    var responseTransactions: LiveData<NetworkResult<TransactionApiResponse>> = _responseTransactions


    fun getTransactions(token: String, page: Int)
    {
        _responseTransactions.value = NetworkResult.Loading()

        viewModelScope.launch{
            try {
                val response = transactionsRepo.getTransactions(token, page)

                if (!response.status)
                {
                    _responseTransactions.value = NetworkResult.Error(response.message,response.code)
                }
                else {
                    _responseTransactions.value = NetworkResult.Success(response)
                }
            } catch (e: Exception) {
                _responseTransactions.value = NetworkResult.Error(e.message,0)
                sendIssueToCrashlytics(e.message.toString(),"Get transactions from Transactions viewModel")
            }
        }
    }

    private val _transactionDetails: MutableStateFlow<TransactionDetailsEntity?> =
        MutableStateFlow(null)
    val transactionDetails: StateFlow<TransactionDetailsEntity?> = _transactionDetails

    fun getTransactionDetails(token: String, transactionId: String)
    {
        viewModelScope.launch {
            try {
                transactionsRepo.getTransactionDetails(token, transactionId)
                    .collectLatest {
                        _transactionDetails.value = it
                    }
            } catch (e: Exception) {
                Log.d("TAG", "diaa getTransactionDetails exception: ${e.message}")
                sendIssueToCrashlytics(e.message.toString(),"Get transaction details from Transactions viewModel")

            }
        }
    }
}
