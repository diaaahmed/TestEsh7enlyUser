package com.esh7enly.esh7enlyuser.viewModel

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esh7enly.data.sharedhelper.SharedHelper
import com.esh7enly.domain.entity.chargebalancerequest.ChargeBalanceRequestPaytabs
import com.esh7enly.domain.repo.ChargeBalanceRepo
import com.esh7enly.esh7enlyuser.click.OnResponseListener
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.KeyPairHandler

import com.esh7enly.esh7enlyuser.util.PayWays
import com.esh7enly.esh7enlyuser.util.sendIssueToCrashlytics
import com.payment.paymentsdk.PaymentSdkConfigBuilder
import com.payment.paymentsdk.integrationmodels.PaymentSdkApms
import com.payment.paymentsdk.integrationmodels.PaymentSdkBillingDetails
import com.payment.paymentsdk.integrationmodels.PaymentSdkConfigurationDetails
import com.payment.paymentsdk.integrationmodels.PaymentSdkLanguageCode
import com.payment.paymentsdk.integrationmodels.PaymentSdkShippingDetails
import com.payment.paymentsdk.integrationmodels.PaymentSdkTransactionClass
import com.payment.paymentsdk.integrationmodels.PaymentSdkTransactionType

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.security.MessageDigest
import javax.inject.Inject

@HiltViewModel
class PaytabsViewModel @Inject constructor(
    private val chargeBalanceRepo: ChargeBalanceRepo,
    private val sharedHelper:SharedHelper
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


    @SuppressLint("NewApi")
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
                KeyPairHandler.generateKeyPair()

                val publicKeyHandler = KeyPairHandler.getPublicKeyString()


                val startSessionResponse = chargeBalanceRepo.startSessionForPay(
                    paymentMethodType = paymentMethodType,
                    transactionType = transactionType,
                    amount = finalAmount,
                    total_amount = amount,
                    ip = ip,
                    uuid = publicKeyHandler
                )

                if (startSessionResponse.isSuccessful) {
                    if (!startSessionResponse.body()?.status!!) {
                        listner.onFailed(
                            startSessionResponse.body()!!.code,
                            startSessionResponse.body()!!.message
                        )

                    } else {

                        Constants.START_SESSION_ID = startSessionResponse.body()!!.data.id

                        println("diaa data encrypted ck ${startSessionResponse.body()?.data?.ck.toString()}")
                        println("diaa data encrypted sk ${startSessionResponse.body()?.data?.sk.toString()}")
                        println("diaa data encrypted pi ${startSessionResponse.body()?.data?.pi.toString()}")

                       try{
                           Constants.CK =  KeyPairHandler.decryptTheData(startSessionResponse.body()?.data?.ck.toString())
                           Constants.SK = KeyPairHandler.decryptTheData(startSessionResponse.body()?.data?.sk.toString())
                           Constants.PI = KeyPairHandler.decryptTheData(startSessionResponse.body()?.data?.pi.toString())
                       }
                       catch (e: Exception)
                       {
                           println("diaa data error ${e.message}")
                       }

                        println("diaa data decrypted ck ${Constants.CK}")
                        println("diaa data decrypted sk ${Constants.SK}")
                        println("diaa data decrypted pi ${Constants.PI}")

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
                    "startSessionForPay from paytabs viewModel"
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
                val paytabsAmountTotal =
                    chargeBalanceRepo.getTotalXPay(
                        amount,
                        paymentMethodType,
                        transactionType
                    )

                if (paytabsAmountTotal.isSuccessful) {
                    if (paytabsAmountTotal.body()!!.status) {
                        listner.onSuccess(
                            paytabsAmountTotal.body()!!.code,
                            paytabsAmountTotal.body()!!.message,
                            paytabsAmountTotal.body()!!.data
                        )
                    } else {
                        listner.onFailed(paytabsAmountTotal.body()!!.code, paytabsAmountTotal.body()!!.message)
                    }
                } else {
                    listner.onFailed(paytabsAmountTotal.code(), paytabsAmountTotal.message())
                }
            } catch (e: Exception) {
                listner.onFailed(Constants.EXCEPTION_CODE, e.message)
                sendIssueToCrashlytics(
                    e.message.toString(),
                    "getTotalxPayFlow from paytabs viewModel"
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
                    "chargeBalanceWithPaytabs from paytabs viewModel"
                )
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
     fun generatePaytabsConfigurationDetails(
        serverKey:String,
        clientKey:String,
        secretKey:String?,
        transactionTitle:String,
        value: String,
        drawable: Drawable?,
        startSessionId: Int
    ): PaymentSdkConfigurationDetails {

        val cartDesc = "Add esh7enly balance" // Description in paytab info
        val currency = "EGP"
        val merchantCountryCode = "EG"
        val amount: Double = value.toDouble()

        val locale = PaymentSdkLanguageCode.AR

        val billingData = PaymentSdkBillingDetails(
            "City",
            countryCode = merchantCountryCode,
            email = sharedHelper.getUserEmail().toString(),
            name = sharedHelper.getStoreName().toString(),
            phone = sharedHelper.getUserPhone().toString(),
            state = "zipcode",
            addressLine = "Egypt",
            zip = ""
        )

        // Customer details
        val shippingData = PaymentSdkShippingDetails(
            "City",
            countryCode = merchantCountryCode,
            email = sharedHelper.getUserEmail().toString(),
            name = "${sharedHelper.getStoreName().toString()} , $startSessionId",
            phone = sharedHelper.getUserPhone().toString(),
            state = "zipcode",
            addressLine = "Egypt",
            zip = ""
        )

        val configData = PaymentSdkConfigBuilder(
            secretKey!!, serverKey, clientKey, amount, currency
        ).setCartDescription(cartDesc)
            .setLanguageCode(locale)
            .setMerchantCountryCode(merchantCountryCode)
            .setMerchantIcon(drawable)
            .hideCardScanner(true)
            .setAlternativePaymentMethods(listOf(PaymentSdkApms.MEEZA_QR))
            .setTransactionType(PaymentSdkTransactionType.SALE)
            .setTransactionClass(PaymentSdkTransactionClass.ECOM)
            .setCartId(Constants.HASH_ID)
            .setBillingData(billingData)
            .setShippingData(shippingData)
            .setScreenTitle(transactionTitle)

        return configData.build()
    }
}