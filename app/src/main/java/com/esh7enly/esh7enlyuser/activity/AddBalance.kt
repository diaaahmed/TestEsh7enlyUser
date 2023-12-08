package com.esh7enly.esh7enlyuser.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle

import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.esh7enly.domain.entity.PaymentPojoModel
import com.esh7enly.domain.entity.TotalAmountPojoModel
import com.esh7enly.domain.entity.chargebalancerequest.ChargeBalanceRequest
import com.esh7enly.domain.entity.totalamountxpayresponse.Data
import com.esh7enly.esh7enlyuser.BuildConfig
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.click.OnResponseListener
import com.esh7enly.esh7enlyuser.databinding.ActivityAddBalanceBinding
import com.esh7enly.esh7enlyuser.util.AppDialogMsg
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.IToolbarTitle
import com.esh7enly.esh7enlyuser.util.NavigateToActivity
import com.esh7enly.esh7enlyuser.util.PayWays
import com.esh7enly.esh7enlyuser.util.Utils
import com.esh7enly.esh7enlyuser.viewModel.PaytabsViewModel
import com.esh7enly.esh7enlyuser.viewModel.XPayViewModel
import com.payment.paymentsdk.PaymentSdkActivity
import com.payment.paymentsdk.PaymentSdkConfigBuilder
import com.payment.paymentsdk.integrationmodels.PaymentSdkApms
import com.payment.paymentsdk.integrationmodels.PaymentSdkBillingDetails
import com.payment.paymentsdk.integrationmodels.PaymentSdkConfigurationDetails
import com.payment.paymentsdk.integrationmodels.PaymentSdkError
import com.payment.paymentsdk.integrationmodels.PaymentSdkLanguageCode
import com.payment.paymentsdk.integrationmodels.PaymentSdkShippingDetails
import com.payment.paymentsdk.integrationmodels.PaymentSdkTransactionClass
import com.payment.paymentsdk.integrationmodels.PaymentSdkTransactionDetails
import com.payment.paymentsdk.integrationmodels.PaymentSdkTransactionType
import com.payment.paymentsdk.sharedclasses.interfaces.CallbackPaymentInterface
import com.xpay.kotlinutils.XpayUtils
import com.xpay.kotlinutils.models.BillingInfo
import com.xpay.kotlinutils.models.PaymentMethods
import com.xpay.kotlinutils.models.ServerSetting
import com.xpay.kotlinutils.models.api.pay.PayData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import kotlin.random.Random

private const val TAG = "AddBalance"

@AndroidEntryPoint
class AddBalance : BaseActivity(),IToolbarTitle
{

    private val ui by lazy{
        ActivityAddBalanceBinding.inflate(layoutInflater)
    }

    private val dialog by lazy {
        AppDialogMsg(this, false)
    }

    private var uuid:String?= null
    private var isCardPayment:Boolean?= null
    private var finalPaymentWay = ""
    var totalAmountStartSession = 0.0

    private val xPayViewModel: XPayViewModel by viewModels()
    private val payTabsViewModel: PaytabsViewModel by viewModels()

    @OptIn(FlowPreview::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)
        initToolBar()

        ui.xPayViewModel = xPayViewModel
        ui.lifecycleOwner = this

        xPayViewModel._buttonClicked.observe(this){ buttonClicked-> finalPaymentWay = buttonClicked }

        ui.amountValue.addTextChangedListener { value->

            if(value.toString().isBlank())
            {
                ui.serviceFee.text = ""
                ui.totalAmount.text = ""
                ui.serviceValue.text = ""
            }
            else
            {
                if(finalPaymentWay== PayWays.BANk.toString() || xPayViewModel.showPhoneNumber.value == false)
                {
                    lifecycleScope.launch { xPayViewModel.amountNumber.emit(value.toString()) }
                    //getTotalAmountForPay(value.toString())
                }
            }
        }

//        lifecycleScope.launch {
//            xPayViewModel.amountNumber.debounce(1000).collect{
//                getTotalAmountForPay(it)
//            }
//        }

//        xPayViewModel._totalXpay.observe(this) { total ->
//            ui.totalAmount.text = total
//            if(total.isNotEmpty())
//            {
//                totalAmountStartSession = total.toDouble()
//            }
//
//            xPayViewModel._fees.observe(this)
//            {fees->
//                ui.serviceFee.text = fees
//                ui.serviceValue.text = ui.amountValue.text.toString()
//            }
//        }

        XpayUtils.serverSetting = ServerSetting.LIVE
        XpayUtils.apiKey = BuildConfig.XPAY_API_KEY_PRODUCTION
        XpayUtils.communityId = BuildConfig.XPAY_COMMUNITY_ID_PRODUCTION
        XpayUtils.apiPaymentId= BuildConfig.XPAY_API_PAYMENT_ID_PRODUCTION.toInt()

        ui.bankWay.setOnClickListener {
            xPayViewModel.setShowNumber(false)

            if (ui.amountValue.text.toString().isNotBlank()) {
                // getTotalAmountForPay(ui.amountValue.text.toString())
            }
            xPayViewModel.buttonClicked.value = PayWays.BANk.toString()
//            ui.serviceFee.text = ""
//            ui.totalAmount.text = ""
//            ui.serviceValue.text = ""
            ui.lineWays.setBackgroundResource(R.drawable.payment_way_background)

        }

        ui.vodafoneCash.setOnClickListener {
            xPayViewModel.setShowNumber(true)
            xPayViewModel.buttonClicked.value = PayWays.CASH.toString()
            ui.lineWays.setBackgroundResource(R.drawable.payment_way_background)
        }

        ui.btnPay.setOnClickListener {

            if(ui.amountValue.text.toString().isEmpty())
            {
                ui.amountValue.error = resources.getString(R.string.required)
            }
            else
            {
                when (finalPaymentWay)
                {
                    PayWays.BANk.toString() -> {
//                        payTabsViewModel.userEmail.value = sharedHelper?.getUserEmail().toString()
//                        payTabsViewModel.userPhone.value = sharedHelper?.getUserPhone().toString()
//                        payTabsViewModel.userName.value = sharedHelper?.getStoreName().toString()


                        val drawable = ContextCompat.getDrawable(this, R.drawable.new_logo_trans_small)

                        paytabsClick(ui.amountValue.text.toString(),
                            drawable)
//                        payTabsViewModel.paytabsClick(this, ui.amountValue.text.toString(),
//                            drawable)

//                        Log.d(TAG, "diaa pay now: Bank")
//
//                        try {
//                            lifecycleScope.launch { payWithXPay()
//                            }
//                        } catch (e: Exception) {
//
//                            dialog.showErrorDialogWithAction(e.message,
//                                resources.getString(R.string.app__ok)
//                            ) {
//                                dialog.cancel()
//
//                            }.show()
//                        }
                    }

                    PayWays.CASH.toString() -> {
                        getTotalWithCash()
                        Log.d(TAG, "diaa pay now: Cash")

                    }
                    else -> {
                        Log.d(TAG, "diaa pay no way")

                    }
                }
            }
        }
    }


    fun paytabsClick(amount:String, drawable: Drawable?)
    {
        val number = Random(900000000000000000).nextInt()

        val configData: PaymentSdkConfigurationDetails =
            generatePaytabsConfigurationDetails(number.toString(),
            amount,drawable)

        PaymentSdkActivity.startCardPayment(this,configData, object :
            CallbackPaymentInterface
        {
            override fun onError(error: PaymentSdkError)
            {
                Log.d("TAG", "diaa onError: ${error.msg}")
                Toast.makeText(this@AddBalance, "Error occure", Toast.LENGTH_SHORT).show()
            }

            override fun onPaymentCancel()
            {
                Log.d("TAG", "diaa cancelled")
                Toast.makeText(this@AddBalance, "Payment canceled", Toast.LENGTH_SHORT).show()

            }

            override fun onPaymentFinish(paymentSdkTransactionDetails: PaymentSdkTransactionDetails)
            {
                Toast.makeText(this@AddBalance,
                    "Paymeny done ${paymentSdkTransactionDetails.cartAmount}",
                    Toast.LENGTH_SHORT).show()
                Log.d("TAG", "diaa token ${paymentSdkTransactionDetails.token}")
                Log.d("TAG", "diaa paymentResult ${paymentSdkTransactionDetails.paymentResult}")
                Log.d("TAG", "diaa cartCurrency ${paymentSdkTransactionDetails.cartCurrency}")
                Log.d("TAG", "diaa cartDescription ${paymentSdkTransactionDetails.cartDescription}")
                Log.d("TAG", "diaa cartAmount ${paymentSdkTransactionDetails.cartAmount}")
                Log.d("TAG", "diaa cartID ${paymentSdkTransactionDetails.cartID}")
                Log.d("TAG", "diaa isSuccess ${paymentSdkTransactionDetails.isSuccess}")
                Log.d("TAG", "diaa isAuthorized ${paymentSdkTransactionDetails.isAuthorized}")
                Log.d("TAG", "diaa isPending ${paymentSdkTransactionDetails.isPending}")
                Log.d("TAG", "diaa isOnHold ${paymentSdkTransactionDetails.isOnHold}")
                Log.d("TAG", "diaa isProcessed ${paymentSdkTransactionDetails.isProcessed}")
                Log.d("TAG", "diaa cardType ${paymentSdkTransactionDetails.paymentInfo!!.cardType}")
                Log.d("TAG", "diaa cardScheme ${paymentSdkTransactionDetails.paymentInfo!!.cardScheme}")
                Log.d("TAG", "diaa transactionType ${paymentSdkTransactionDetails.transactionType}")
                Log.d("TAG", "diaa transactionReference ${paymentSdkTransactionDetails.transactionReference}")
                Log.d("TAG", "diaa redirectUrl ${paymentSdkTransactionDetails.redirectUrl}")
                Log.d("TAG", "diaa paymentDescription ${paymentSdkTransactionDetails.paymentInfo!!.paymentDescription}")
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
        val serverKey = BuildConfig.SERVER_KEY
        // Here you can enter your client key from payabs account
        val clientKey = BuildConfig.CLIENT_KEY
        val transactionTitle = resources.getString(R.string.paytabs_title)
        val cartDesc = "Add esh7enly balance" // Description in paytab info
        val currency = "EGP"
        val merchantCountryCode = "EG"
        val amount: Double = value.toDouble()
        val locale = if(Constants.LANG == "ar") PaymentSdkLanguageCode.AR else PaymentSdkLanguageCode.EN

        val billingData = PaymentSdkBillingDetails(
            "City",
            merchantCountryCode,
            sharedHelper?.getUserEmail().toString(),
            sharedHelper?.getStoreName().toString(),
            sharedHelper?.getUserPhone().toString(), "zipcode",
            "Egypt", ""
        )

        // Customer details
        val shippingData = PaymentSdkShippingDetails(
            "City",
            merchantCountryCode,
            sharedHelper?.getUserEmail().toString(),
            sharedHelper?.getStoreName().toString(),
            sharedHelper?.getUserPhone().toString(), "zipcode",
            "Egypt", ""
        )


        val configData = PaymentSdkConfigBuilder(
            profileId, serverKey, clientKey, amount, currency
        ).setCartDescription(cartDesc)
            .setLanguageCode(locale)
            .setMerchantCountryCode(merchantCountryCode)
            .setMerchantIcon(drawable)
            .hideCardScanner(true)
            .setAlternativePaymentMethods(listOf(PaymentSdkApms.MEEZA_QR))
            .setTransactionType(PaymentSdkTransactionType.SALE)
            .setTransactionClass(PaymentSdkTransactionClass.ECOM)
            .setCartId(orderNum)
            .setBillingData(billingData)
            .setShippingData(shippingData)
            .setScreenTitle(transactionTitle)

        return configData.build()
    }

    private fun getTotalWithCash() {

        pDialog.show()

        lifecycleScope.launch {

            val params = TotalAmountPojoModel.Params("billing_account",ui.phoneNumber.text.toString())

            val totalAmountPojoModel = TotalAmountPojoModel(Constants.IMEI,
                3968,ui.amountValue.text.toString(), mutableListOf(params))

            serviceViewModel.getTotalAmount(sharedHelper?.getUserToken().toString(),totalAmountPojoModel,
                object : OnResponseListener {
                    override fun onSuccess(code: Int, msg: String?, obj: Any?)
                    {
                        pDialog.dismiss()

                        val paymentPojoModel =
                            PaymentPojoModel(
                                Constants.IMEI,
                                "",
                               3968,
                                ui.amountValue.text.toString(),
                                mutableListOf(params)
                            )

                        payWithCash(paymentPojoModel)

                    }

                    override fun onFailed(code: Int, msg: String?)
                    {
                        showFailedPay(msg,code)
                    }
                })
        }
    }

    private fun payWithCash(paymentPojoModel: PaymentPojoModel) {
        pDialog.show()

        lifecycleScope.launch {
            serviceViewModel.pay(sharedHelper?.getUserToken().toString(),paymentPojoModel,
                object : OnResponseListener {
                    override fun onSuccess(code: Int, msg: String?, obj: Any?)
                    {
                        pDialog.dismiss()

                        dialog.showSuccessDialog(resources.getString(R.string.balance_added),resources.getString(R.string.app__ok))
                        {
                            dialog.cancel()
                            finish()

                        }
                        dialog.show()
                    }

                    override fun onFailed(code: Int, msg: String?)
                    {
                        showFailedPay(msg,code)
                        Log.d(TAG, "diaa payWithCash error: $msg")
                    }
                })
        }
    }


    override fun initToolBar() {
        ui.addBalanceToolbar.title = resources.getString(R.string.add_balance)

        ui.addBalanceToolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun getTotalAmount()
    {
        lifecycleScope.launch {

            xPayViewModel.getTotalXPayFlow(sharedHelper?.getUserToken().toString(),
            ui.amountValue.text.toString(), object : OnResponseListener {
                    override fun onSuccess(code: Int, msg: String?, obj: Any?)
                    {
                        pDialog.cancel()

                        val data = obj as Data

                        val amount = java.lang.String.format(
                            " • %s %s %s",
                            getString(R.string.dialog_amount),
                            Utils.format(ui.amountValue.text.toString().toDouble()),
                            getString(R.string.egp)
                        )

                        val fees = java.lang.String.format(
                            " • %s %s %s",
                            getString(R.string.service_fees),
                            Utils.format(data.fees),
                            getString(R.string.egp)
                        )

                        val total = java.lang.String.format(
                            " • %s %s %s",
                            getString(R.string.dialog_total_amount),
                            Utils.format(data.amount),
                            getString(R.string.egp)
                        )

                        dialog.showSuccessDialogWithAction(
                            resources.getString(R.string.confirmation_title),
                            String.format(
                                "%s \n %s \n %s",
                                amount,
                                fees,
                                total
                            ),resources.getString(R.string.app__ok),resources.getString(R.string.app__cancel)
                        ) {
                            dialog.cancel()

                            pDialog.show()

                            lifecycleScope.launch {
                                xPayViewModel.startSessionForPay(sharedHelper?.getUserToken()
                                    .toString(),
                                    data.amount.toString(),
                                    "69",
                                    object : OnResponseListener {
                                        override fun onSuccess(code: Int, msg: String?, obj: Any?)
                                        {
                                            lifecycleScope.launch {
                                                try {

                                                    // XpayUtils.prepareAmount(data.amount)
                                                    XpayUtils.prepareAmount(ui.amountValue.text.toString().toDouble())
                                                    XpayUtils.payUsing = PaymentMethods.CARD

                                                    // val totalAmount = PaymentMethods.CARD

                                                    // set billing information
                                                    XpayUtils.billingInfo = BillingInfo(
                                                        sharedHelper?.getStoreName().toString(),
                                                        sharedHelper?.getUserEmail().toString(),
                                                        "+2" + sharedHelper?.getUserPhone()
                                                            .toString()
                                                    )

                                                    // make payment
                                                    val paymentResponse = XpayUtils.pay()
                                                    checkPaymentType(paymentResponse)

                                                } catch (e: Exception) {
                                                    Log.d(
                                                        TAG,
                                                        "diaa xpay error here new:${e.message} "
                                                    )
                                                    showFailedPay(e.message, code)
                                                }
                                            }
                                        }

                                        override fun onFailed(code: Int, msg: String?) {
                                            Log.d(TAG, "diaa onFailed: $msg")
                                            showFailedPay(msg, code)
                                        }
                                    })
                            }

                        }.show()

                    }

                    override fun onFailed(code: Int, msg: String?)
                    {
                        pDialog.cancel()
                        dialog.showErrorDialogWithAction(msg,
                            resources.getString(R.string.app__ok)
                        ) {
                            dialog.cancel()
                            if (code.toString() == Constants.CODE_UNAUTH || code.toString() == Constants.CODE_HTTP_UNAUTHORIZED)
                            {
                                NavigateToActivity.navigateToMainActivity(this@AddBalance)
                            }

                        }.show()
                    }
                })
        }
    }

    private fun payWithXPay()
    {
        pDialog.show()

        getTotalAmount()

//        Log.d(TAG, "diaa payWithXPay: ${sharedHelper?.getUserPhone().toString()}")
//        Log.d(TAG, "diaa payWithXPay totalAmount: $totalAmountStartSession")
//
//        xPayViewModel.startSessionForPay(sharedHelper?.getUserToken().toString(),
//            totalAmountStartSession.toString(),
//                "69",
//                object : OnResponseListener
//                {
//                    override fun onSuccess(code: Int, msg: String?, obj: Any?)
//                    {
//                            lifecycleScope.launch {
//                                try {
//                                    Log.d(TAG, "diaa payWithXPay total for xpay: $totalAmountStartSession")
//                                    XpayUtils.prepareAmount(totalAmountStartSession)
//                                    XpayUtils.payUsing = PaymentMethods.CARD
//
//                                   // val totalAmount = PaymentMethods.CARD
//
//                                    // set billing information
//                                    XpayUtils.billingInfo = BillingInfo(sharedHelper?.getStoreName().toString(),
//                                        sharedHelper?.getUserEmail().toString(),"+2"+sharedHelper?.getUserPhone().toString())
//
//                                    // make payment
//                                    val paymentResponse = XpayUtils.pay()
//                                    checkPaymentType(paymentResponse)
//
//                                } catch (e: Exception)
//                                {
//                                    Log.d(TAG, "diaa xpay error here new:${e.message} ")
//                                    showFailedPay(e.message,code)
//                                }
//                            }
//                    }
//
//                    override fun onFailed(code: Int, msg: String?)
//                    {
//                        Log.d(TAG, "diaa onFailed: $msg")
//                        showFailedPay(msg,code)
//                    }
//                })
    }

    private fun checkPaymentType(paymentResponse: PayData?)
    {
        uuid = paymentResponse?.transaction_uuid

        // If paymentResponse.iframe_url is not null that means we use Card(Master - Visa)
        // So we go to the iframe
        paymentResponse?.iframe_url.let { iframeUrl->
            isCardPayment = true
            val builder = CustomTabsIntent.Builder()

            builder.setToolbarColor(ContextCompat.getColor(this@AddBalance,R.color.colorPrimary))
           // builder.setShowTitle(true)
          //  builder.setUrlBarHidingEnabled(true)
            val customTabsIntent: CustomTabsIntent = builder.build()
            customTabsIntent.launchUrl(this@AddBalance, Uri.parse(iframeUrl))
        }
    }


    @SuppressLint("SetTextI18n")
    override fun onRestart()
    {
        super.onRestart()

        // Receive transaction information
        lifecycleScope.launch {
            try {
                uuid.let { uui->
                   // dialog?.cancel()
                    val res = XpayUtils.getTransaction(uui!!)

                    val chargeBalanceRequest = ChargeBalanceRequest(id = Constants.START_SESSION_ID,
                        amount = ui.amountValue.text.toString(),
                    total_amount = res?.total_amount.toString(), card_id = res?.id.toString(),
                    total_amount_currency = res?.total_amount_currency.toString(),
                        total_amount_piasters = res?.total_amount_piasters.toString(),
                    status = res?.status.toString(), payment_for = res?.payment_for.toString(),
                    uuid = res?.uuid.toString(), member_id = res?.member_id.toString(),
                        quantity = res?.quantity.toString())


                    Log.d(TAG, "diaa member_id: ${res?.member_id.toString()}")
                    Log.d(TAG, "diaa quantity: ${res?.quantity.toString()}")
                    Log.d(TAG, "diaa totalamountcpay: ${res?.total_amount}")

                    xPayViewModel.chargeBalance(sharedHelper?.getUserToken().toString(),
                        chargeBalanceRequest,
                        object : OnResponseListener
                        {
                            override fun onSuccess(code: Int, msg: String?, obj: Any?)
                            {
                                pDialog.cancel()

                                dialog.showSuccessDialog(resources.getString(R.string.balance_added),resources.getString(R.string.app__ok))
                                {
                                    dialog.cancel()
                                    finish()

                                }
                                dialog.show()

                                Log.d(TAG, "diaa on charge success: $msg")
                            }

                            override fun onFailed(code: Int, msg: String?)
                            {
                                showFailedPay(msg,code)

                                Log.d(TAG, "diaa on charge failed: $msg")
                            }

                        })

//                    res?.status.let {status->
//                        ui.serviceFee.text = status
//                    }

                }
            } catch (e: Exception) {
                Log.d(TAG, "diaa onRestart error: ${e.message}")
            }
        }
    }

    private fun showFailedPay(msg: String?, code: Int)
    {
        pDialog.cancel()
        dialog.showErrorDialogWithAction(
            msg, resources.getString(R.string.app__ok)
        ) {
            dialog.cancel()

            if (code.toString() == Constants.CODE_UNAUTH ||
                code.toString() == Constants.CODE_HTTP_UNAUTHORIZED
            ) {
                NavigateToActivity.navigateToMainActivity(this@AddBalance)
            }
        }.show()
    }

}