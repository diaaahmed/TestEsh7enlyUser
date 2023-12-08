package com.esh7enly.esh7enlyuser.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esh7enly.domain.entity.chargebalancerequest.ChargeBalanceRequest
import com.esh7enly.domain.usecase.XPayUseCase
import com.esh7enly.esh7enlyuser.click.OnResponseListener
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.PayWays
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class XPayViewModel @Inject constructor(private val xPayUseCase: XPayUseCase) : ViewModel()
{

    var buttonClicked: MutableLiveData<String> = MutableLiveData(PayWays.BANk.toString())
    var _buttonClicked: LiveData<String> = buttonClicked

    private var _showPhoneNumber:MutableLiveData<Boolean> = MutableLiveData(false)
    var showPhoneNumber:LiveData<Boolean>  = _showPhoneNumber

    fun setShowNumber(isShow:Boolean)
    {
        _showPhoneNumber.postValue(isShow)
    }

    suspend fun startSessionForPay(
        token: String,
        amount: String,
        ip: String,
        listner: OnResponseListener
    ) {
        viewModelScope.launch {
            val startSessionResponse = xPayUseCase.startSessionForPay(token, amount, ip)

            if (startSessionResponse.isSuccessful)
            {
                if (!startSessionResponse.body()?.status!!)
                {
                    listner.onFailed(
                        startSessionResponse.body()!!.code,
                        startSessionResponse.body()!!.message
                    )
                    Log.d(
                        "TAG",
                        "diaa startSessionForPay failed: ${startSessionResponse.body()!!.message}"
                    )
                } else {

                    Constants.START_SESSION_ID = startSessionResponse.body()!!.data.id
                    Log.d(
                        "TAG",
                        "diaa startSessionForPay success: ${startSessionResponse.body()!!.data.id}"
                    )
                    listner.onSuccess(
                        startSessionResponse.body()!!.code,
                        startSessionResponse.body()!!.message, startSessionResponse.body()!!.data.id
                    )
                }

            } else {
                Log.d(
                    "TAG",
                    "diaa startSessionForPay failed catch: ${startSessionResponse.message()}"
                )
                listner.onFailed(
                    startSessionResponse.code(),
                    startSessionResponse.message()
                )

            }
        }

    }

    private var totalXPay: MutableLiveData<String> = MutableLiveData("")
    var _totalXpay = totalXPay

    private var fees: MutableLiveData<String> = MutableLiveData("")
    var _fees = fees

    var amountNumber = MutableStateFlow("")

    suspend fun getTotalXPayFlow(token: String, amount: String,listner: OnResponseListener) {
        viewModelScope.launch {
            val xPayTotal = xPayUseCase.getTotalXPay(token, amount)

            if(xPayTotal.isSuccessful)
            {
                if(xPayTotal.body()!!.status)
                {
                    listner.onSuccess(xPayTotal.body()!!.code,xPayTotal.body()!!.message,xPayTotal.body()!!.data)
                }
                else
                {
                    listner.onFailed(xPayTotal.body()!!.code,xPayTotal.body()!!.message)
                }
            }
            else
            {
                listner.onFailed(xPayTotal.code(),xPayTotal.message())
            }
        }
    }


    suspend fun chargeBalance(token:String,chargeBalanceRequest: ChargeBalanceRequest,listner: OnResponseListener) {
       viewModelScope.launch {
           val charge = xPayUseCase.chargeBalance(token,chargeBalanceRequest)

           if(charge.isSuccessful)
           {
               if(!charge.body()!!.status)
               {
                   listner.onFailed(charge.body()!!.code,charge.body()?.message)
               }
               else
               {
                   listner.onSuccess(charge.body()!!.code,charge.body()?.message,charge.body()!!.data)
               }
           }
           else
           {
               listner.onFailed(charge.code(),charge.message())
           }
       }
    }
}