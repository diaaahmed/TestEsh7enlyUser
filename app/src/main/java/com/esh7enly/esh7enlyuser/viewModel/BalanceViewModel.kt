package com.esh7enly.esh7enlyuser.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esh7enly.data.repo.UserRepo
import com.esh7enly.domain.entity.depositsresponse.DepositResponse
import com.esh7enly.esh7enlyuser.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BalanceViewModel @Inject constructor(
    private val userRepo: UserRepo,

    ): ViewModel()
{
    private var _responseDeposits: MutableLiveData<NetworkResult<DepositResponse>> = MutableLiveData()

    var responseDeposits: LiveData<NetworkResult<DepositResponse>> = _responseDeposits

    fun getNewDeposits(token: String, page: Int)
    {
        _responseDeposits.value = NetworkResult.Loading()

        viewModelScope.launch {
            try{
                val deposits = userRepo.getDeposits(token,page)

                if(deposits.isSuccessful)
                {
                    if(!deposits.body()!!.status)
                    {
                        _responseDeposits.value = NetworkResult.Error(deposits.body()!!.message)

                    }
                    else
                    {
                        _responseDeposits.value = NetworkResult.Success(deposits.body()!!)
                    }
                }
                else
                {
                    _responseDeposits.value = NetworkResult.Error(deposits.message())

                }
            }
            catch (e: Exception)
            {
                _responseDeposits.value = NetworkResult.Error(e.message)

            }

        }
    }

    private val _balance:MutableLiveData<String> = MutableLiveData("")
    val balance:LiveData<String> = _balance

    fun getWalletsUser(token:String) {
        viewModelScope.launch {
           try{
               val response = userRepo.getUserWallet(token)

               if(response.isSuccessful && response.body()?.status == true)
               {
                   _balance.value = response.body()!!.data[0].balance
               }
               else
               {
                   _balance.value = "0.0"

               }
           }
           catch (e: Exception){
               _balance.value = e.message

           }
        }
    }
}