package com.esh7enly.esh7enlyuser.viewModel

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.esh7enly.esh7enlyuser.BuildConfig
import com.payment.paymentsdk.PaymentSdkActivity
import com.payment.paymentsdk.PaymentSdkConfigBuilder
import com.payment.paymentsdk.integrationmodels.PaymentSdkBillingDetails
import com.payment.paymentsdk.integrationmodels.PaymentSdkConfigurationDetails
import com.payment.paymentsdk.integrationmodels.PaymentSdkError
import com.payment.paymentsdk.integrationmodels.PaymentSdkLanguageCode
import com.payment.paymentsdk.integrationmodels.PaymentSdkShippingDetails
import com.payment.paymentsdk.integrationmodels.PaymentSdkTransactionClass
import com.payment.paymentsdk.integrationmodels.PaymentSdkTransactionDetails
import com.payment.paymentsdk.integrationmodels.PaymentSdkTransactionType
import com.payment.paymentsdk.sharedclasses.interfaces.CallbackPaymentInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class PaytabsViewModel @Inject constructor(): ViewModel()
{
    var userEmail:MutableLiveData<String> = MutableLiveData()
    var userPhone:MutableLiveData<String> = MutableLiveData()
    var userName:MutableLiveData<String> = MutableLiveData()

    fun paytabsClick(context: Context, amount:String,drawable: Drawable?)
    {
        val number = Random(0).nextInt()

        val configData: PaymentSdkConfigurationDetails = generatePaytabsConfigurationDetails(number.toString(),
            amount,drawable)

        PaymentSdkActivity.startCardPayment(context as Activity,configData, object :
            CallbackPaymentInterface
        {
            override fun onError(error: PaymentSdkError)
            {
                Log.d("TAG", "diaa onError: ${error.msg}")
                Toast.makeText(context, "Error occure", Toast.LENGTH_SHORT).show()
            }

            override fun onPaymentCancel()
            {
                Log.d("TAG", "diaa cancelled")
                Toast.makeText(context, "Payment canceled", Toast.LENGTH_SHORT).show()

            }

            override fun onPaymentFinish(paymentSdkTransactionDetails: PaymentSdkTransactionDetails)
            {
                Toast.makeText(context,
                    "Paymeny done ${paymentSdkTransactionDetails.cartAmount}",
                    Toast.LENGTH_SHORT).show()

                Log.d("TAG", "diaa finish ${paymentSdkTransactionDetails.token}" +
                        " and ${paymentSdkTransactionDetails.paymentResult}")
            }
        })
    }

    fun generatePaytabsConfigurationDetails(
        orderNum: String,
        value: String,
        drawable: Drawable?
    ): PaymentSdkConfigurationDetails {
        // Here you can enter your profile id from payabs account
        val profileId = "122125"
        // Here you can enter server key from payabs account
        val serverKey = BuildConfig.SERVER_KEY_TEST
        // Here you can enter your client key from payabs account
        val clientKey = BuildConfig.CLIENT_KEY_TEST
        val transactionTitle = "Pay now with esh7enly"
        val cartDesc = "Test pay" // Description in paytab info
        val currency = "EGP"
        val merchantCountryCode = "EG"
        val amount: Double = value.toDouble()
        val locale = PaymentSdkLanguageCode.EN

        val billingData = PaymentSdkBillingDetails(
            "City",
            merchantCountryCode,
            userEmail.value,
            userName.value,
            userPhone.value, "zipcode",
            "Egypt", ""
        )

        // Customer details
        val shippingData = PaymentSdkShippingDetails(
            "City",
            merchantCountryCode,
            userEmail.value,
            userName.value,
            userPhone.value, "zipcode",
            "Egypt", ""
        )


        val configData = PaymentSdkConfigBuilder(
            profileId, serverKey, clientKey, amount, currency
        ).setCartDescription(cartDesc)
            .setLanguageCode(locale)
            .setMerchantCountryCode(merchantCountryCode)
            .setMerchantIcon(drawable)
            .showBillingInfo(true)
            .showShippingInfo(true)
            .setTransactionType(PaymentSdkTransactionType.SALE)
            .setTransactionClass(PaymentSdkTransactionClass.ECOM)
            .setCartId(orderNum)
            .setBillingData(billingData)
            .setShippingData(shippingData)
            .setScreenTitle(transactionTitle)

        return configData.build()
    }

}