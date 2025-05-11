package com.esh7enly.esh7enlyuser.viewModel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esh7enly.domain.entity.chargebalancerequest.ChargeBalanceRequestPaytabs
import com.esh7enly.domain.repo.ChargeBalanceRepo
import com.esh7enly.esh7enlyuser.click.OnResponseListener
import com.esh7enly.esh7enlyuser.util.Constants

import com.esh7enly.esh7enlyuser.util.PayWays
import com.esh7enly.esh7enlyuser.util.sendIssueToCrashlytics

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.security.MessageDigest
import javax.inject.Inject

@HiltViewModel
class PaytabsViewModel @Inject constructor(
    private val chargeBalanceRepo: ChargeBalanceRepo
) : ViewModel() {

    var buttonClicked: MutableLiveData<String> = MutableLiveData(PayWays.BANk.toString())
    var _buttonClicked: LiveData<String> = buttonClicked

    private var _showPhoneNumberNew: MutableLiveData<String> =
        MutableLiveData(PayWays.BANk.toString())
    var showPhoneNumberNew: LiveData<String> = _showPhoneNumberNew

    private var _showPhoneNumber: MutableLiveData<Boolean> = MutableLiveData(false)
    var showPhoneNumber: LiveData<Boolean> = _showPhoneNumber

    fun setShowNumber(isShow: Boolean) {
        _showPhoneNumber.postValue(isShow)
    }

    fun setShowNumberNew(type: String) {
        _showPhoneNumberNew.postValue(type)
    }


    @RequiresApi(Build.VERSION_CODES.M)
    suspend fun startSessionForPay(
        finalAmount: String,
        paymentMethodType: String,
        transactionType: String,
        amount: String,
        ip: String,
        listner: OnResponseListener
    ) {
        viewModelScope.launch {
            try {
                val startSessionResponse = chargeBalanceRepo.startSessionForPay(
                    paymentMethodType = paymentMethodType,
                    transactionType = transactionType,
                    amount = finalAmount,
                    total_amount = amount,
                    ip = ip
                )

                if (startSessionResponse.isSuccessful) {
                    if (!startSessionResponse.body()?.status!!) {
                        listner.onFailed(
                            startSessionResponse.body()!!.code,
                            startSessionResponse.body()!!.message
                        )

                    } else {

                        Constants.START_SESSION_ID = startSessionResponse.body()!!.data.id

                        val normalId = startSessionResponse.body()!!.data.id

                        val finalData =
                            normalId.toString() +
                                    finalAmount + baseLockWord() +
                                    baseLock() + startSessionResponse.body()!!.data.hash_id

                        val generated = newmd5(finalData)

                        Constants.HASH_GENERATED = generated

                        Constants.HASH_ID = startSessionResponse.body()!!.data.hash_id

                        Log.d("TAG", "diaa has_id after converted : $generated")
                        Log.d("TAG", "diaa hash_id : ${Constants.HASH_ID}")


                        listner.onSuccess(
                            startSessionResponse.body()!!.code,
                            startSessionResponse.body()!!.message,
                            startSessionResponse.body()!!.data.id
                        )
                    }

                } else {
                    listner.onFailed(
                        startSessionResponse.code(),
                        startSessionResponse.message()
                    )
                }
            } catch (e: Exception) {
                sendIssueToCrashlytics(
                    e.message.toString(),
                    "startSessionForPay from xPay viewModel"
                )
            }
        }

    }


    private fun newmd5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(
            1, md.digest(input.toByteArray())
        ).toString(16)
            .padStart(32, '0')
    }

    var amountNumber = MutableStateFlow("")

    suspend fun getTotalPay(
        paymentMethodType: String,
        transactionType: String, amount: String, listner: OnResponseListener
    ) {
        viewModelScope.launch {
            try {
                val xPayTotal =
                    chargeBalanceRepo.getTotalXPay(
                        amount,
                        paymentMethodType,
                        transactionType
                    )

                if (xPayTotal.isSuccessful) {
                    if (xPayTotal.body()!!.status) {
                        listner.onSuccess(
                            xPayTotal.body()!!.code,
                            xPayTotal.body()!!.message,
                            xPayTotal.body()!!.data
                        )
                    } else {
                        listner.onFailed(xPayTotal.body()!!.code, xPayTotal.body()!!.message)
                    }
                } else {
                    listner.onFailed(xPayTotal.code(), xPayTotal.message())
                }
            } catch (e: Exception) {
                listner.onFailed(Constants.EXCEPTION_CODE, e.message)
                sendIssueToCrashlytics(
                    e.message.toString(),
                    "getTotalxPayFlow from xPay viewModel"
                )

            }
        }
    }


    private external fun pay(): String
    private external fun baseLock(): String
    private external fun baseLockWord(): String

    init {
        System.loadLibrary("esh7enlyuser")
    }

    suspend fun checkWalletStatus(
        chargeBalanceRequest: ChargeBalanceRequestPaytabs,
        listner: OnResponseListener
    ) {
        viewModelScope.launch {

            try {
                val checkWalletStatusResponse =
                    chargeBalanceRepo.checkWalletStatus(chargeBalanceRequest)

                if (checkWalletStatusResponse.isSuccessful) {

                    if (checkWalletStatusResponse.body()!!.status) {
                        listner.onSuccess(
                            checkWalletStatusResponse.code(),
                            checkWalletStatusResponse.message(),
                            checkWalletStatusResponse.body()
                        )

                    } else {
                        listner.onFailed(
                            checkWalletStatusResponse.body()!!.code,
                            checkWalletStatusResponse.body()!!.message
                        )
                    }

                } else {
                    listner.onFailed(
                        checkWalletStatusResponse.code(),
                        checkWalletStatusResponse.message()
                    )
                }
            } catch (e: Exception) {
                listner.onFailed(Constants.EXCEPTION_CODE, e.message)
                sendIssueToCrashlytics(
                    e.message.toString(),
                    "checkWalletStatus from paytabs viewModel"
                )
            }
        }
    }


    suspend fun chargeBalanceWithPaytabs(
        chargeBalanceRequest: ChargeBalanceRequestPaytabs,
        listner: OnResponseListener
    ) {
        viewModelScope.launch {
            try {
                val charge = chargeBalanceRepo.chargeBalanceWithPaytabs(
                    url = pay(), chargeBalanceRequest = chargeBalanceRequest
                )

                if (charge.isSuccessful) {
                    if (!charge.body()!!.status) {
                        listner.onFailed(charge.body()!!.code, charge.body()?.message)
                    } else {
                        listner.onSuccess(
                            charge.body()!!.code,
                            charge.body()?.message,
                            charge.body()!!.data
                        )
                    }
                } else {
                    listner.onFailed(charge.code(), charge.message())
                }
            } catch (e: Exception) {
                sendIssueToCrashlytics(
                    e.message.toString(),
                    "chargeBalanceWithPaytabs from xPay viewModel"
                )
            }

        }
    }
}