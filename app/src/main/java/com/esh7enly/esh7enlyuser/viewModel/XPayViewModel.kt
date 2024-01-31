package com.esh7enly.esh7enlyuser.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esh7enly.data.repo.XPayRepo
import com.esh7enly.domain.entity.chargebalancerequest.ChargeBalanceRequestPaytabs
import com.esh7enly.esh7enlyuser.click.OnResponseListener
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.PayWays
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class XPayViewModel @Inject constructor(private val xPayRepo: XPayRepo) : ViewModel()
{
    var buttonClicked: MutableLiveData<String> = MutableLiveData(PayWays.BANk.toString())
    var _buttonClicked: LiveData<String> = buttonClicked

    private var _showPhoneNumberNew:MutableLiveData<String> = MutableLiveData(PayWays.BANk.toString())
    var showPhoneNumberNew:LiveData<String>  = _showPhoneNumberNew

    fun setShowNumberNew(type:String)
    {
        _showPhoneNumberNew.postValue(type)
    }


    suspend fun startSessionForPay(
        payment_method_type:String,
        transaction_type:String,
        token: String,
        amount: String,
        ip: String,
        listner: OnResponseListener
    ) {
        viewModelScope.launch {
            val startSessionResponse = xPayRepo.startSessionForPay(
                payment_method_type,transaction_type,token, amount, ip)

            if (startSessionResponse.isSuccessful)
            {
                if (!startSessionResponse.body()?.status!!)
                {
                    listner.onFailed(
                        startSessionResponse.body()!!.code,
                        startSessionResponse.body()!!.message
                    )

                } else {

                    Constants.START_SESSION_ID = startSessionResponse.body()!!.data.id

                    listner.onSuccess(
                        startSessionResponse.body()!!.code,
                        startSessionResponse.body()!!.message, startSessionResponse.body()!!.data.id
                    )
                }

            } else {
                listner.onFailed(
                    startSessionResponse.code(),
                    startSessionResponse.message()
                )

            }
        }

    }

    var amountNumber = MutableStateFlow("")

    suspend fun getTotalXPayFlow(
        token: String, payment_method_type: String,
        transaction_type:String,amount: String,listner: OnResponseListener) {
        viewModelScope.launch {
            try{
                val xPayTotal = xPayRepo.getTotalXPay(token, amount,payment_method_type, transaction_type)

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
            catch (e:Exception)
            {
                listner.onFailed(5000,e.message)

            }
        }
    }


    suspend fun chargeBalanceWithPaytabs(token:String,
                                         chargeBalanceRequest: ChargeBalanceRequestPaytabs,
                                         listner: OnResponseListener) {
        viewModelScope.launch {
            val charge = xPayRepo.chargeBalanceWithPaytabs(token,chargeBalanceRequest)

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