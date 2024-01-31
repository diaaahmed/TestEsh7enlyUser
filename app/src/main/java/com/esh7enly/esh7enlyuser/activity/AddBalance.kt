package com.esh7enly.esh7enlyuser.activity

import android.graphics.drawable.Drawable
import android.os.Bundle

import android.util.Log
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.esh7enly.domain.entity.chargebalancerequest.ChargeBalanceRequestPaytabs
import com.esh7enly.domain.entity.totalamountxpayresponse.Data
import com.esh7enly.esh7enlyuser.BuildConfig
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.click.OnResponseListener
import com.esh7enly.esh7enlyuser.databinding.ActivityAddBalanceBinding
import com.esh7enly.esh7enlyuser.util.AppDialogMsg
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.GatewayMethod
import com.esh7enly.esh7enlyuser.util.GatewayTransactionType
import com.esh7enly.esh7enlyuser.util.IToolbarTitle
import com.esh7enly.esh7enlyuser.util.NavigateToActivity
import com.esh7enly.esh7enlyuser.util.PayWays
import com.esh7enly.esh7enlyuser.util.PaymentStatus
import com.esh7enly.esh7enlyuser.util.Utils
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.random.Random

private const val TAG = "AddBalance"

@AndroidEntryPoint
class AddBalance : BaseActivity(), IToolbarTitle,CallbackPaymentInterface {

    private val ui by lazy {
        ActivityAddBalanceBinding.inflate(layoutInflater)
    }

    private var transactionTypeFinal = ""

    private val dialog by lazy {
        AppDialogMsg(this, false)
    }

    private var finalPaymentWay = ""

    private val xPayViewModel: XPayViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)
        initToolBar()

        ui.xPayViewModel = xPayViewModel
        ui.lifecycleOwner = this

        xPayViewModel._buttonClicked.observe(this) { buttonClicked ->
            finalPaymentWay = buttonClicked
        }

        ui.amountValue.addTextChangedListener { value ->

            if (value.toString().isBlank()) {
                ui.serviceFee.text = ""
                ui.totalAmount.text = ""
                ui.serviceValue.text = ""
            } else {
                if (finalPaymentWay == PayWays.BANk.toString()) {
                    lifecycleScope.launch { xPayViewModel.amountNumber.emit(value.toString()) }
                }
            }
        }

        ui.bankWay.setOnClickListener {

            xPayViewModel.setShowNumberNew(PayWays.BANk.toString())
            xPayViewModel.buttonClicked.value = PayWays.BANk.toString()
            ui.lineWays.setBackgroundResource(R.drawable.payment_way_background)

        }

        ui.digitalWalletWay.setOnClickListener {

            xPayViewModel.setShowNumberNew(PayWays.WALLET.toString())
            xPayViewModel.buttonClicked.value = PayWays.WALLET.toString()
            ui.lineWays.setBackgroundResource(R.drawable.payment_way_background)
        }


        ui.btnPay.setOnClickListener {

            if (ui.amountValue.text.toString().isEmpty()) {
                ui.amountValue.error = resources.getString(R.string.required)
            } else {
                when (finalPaymentWay) {
                    PayWays.BANk.toString() -> {
                        pDialog.show()

                        getTotalAmount(GatewayTransactionType.visa.toString())
                    }

                    PayWays.WALLET.toString() -> {
                        pDialog.show()

                        getTotalAmount(GatewayTransactionType.wallet.toString())
                    }

                    else -> {
                        Log.d(TAG, "diaa pay no way")

                    }
                }
            }
        }
    }


    private fun paytabsClick(
        transactionType:String, totalAmount: String, drawable: Drawable?)
    {
        val number = Random(9000000000000000000).nextInt()

        val configData: PaymentSdkConfigurationDetails =
            generatePaytabsConfigurationDetails(
                number.toString(),
                totalAmount, drawable
            )

        when(transactionType)
        {
            GatewayTransactionType.visa.toString() ->{
                transactionTypeFinal = GatewayTransactionType.visa.toString()

                PaymentSdkActivity.startCardPayment(this,configData,this)

            }

            GatewayTransactionType.wallet.toString() ->{
                transactionTypeFinal = GatewayTransactionType.wallet.toString()

                PaymentSdkActivity.startAlternativePaymentMethods(this,configData,this)
            }

        }
    }

    private fun generatePaytabsConfigurationDetails(
        orderNum: String,
        value: String,
        drawable: Drawable?
    ): PaymentSdkConfigurationDetails {
         val profileId = BuildConfig.PROFILE_ID_PRODUCTION
        //val profileId = "135102"
        val serverKey = BuildConfig.SERVER_KEY_PRODUCTION
        val clientKey = BuildConfig.CLIENT_KEY_PRODUCTION
        val transactionTitle = resources.getString(R.string.paytabs_title)
        val cartDesc = "Add esh7enly balance" // Description in paytab info
        val currency = "EGP"
        val merchantCountryCode = "EG"
        val amount: Double = value.toDouble()
        val locale =
            if (Constants.LANG == "ar") PaymentSdkLanguageCode.AR else PaymentSdkLanguageCode.EN

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

    override fun initToolBar() {
        ui.addBalanceToolbar.title = resources.getString(R.string.add_balance)

        ui.addBalanceToolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun getTotalAmount(transactionType:String) {
        lifecycleScope.launch {

            xPayViewModel.getTotalXPayFlow(sharedHelper?.getUserToken().toString(),
                payment_method_type = GatewayMethod.paytabs.toString(),
                transaction_type = transactionType,
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
                            ),
                            resources.getString(R.string.app__ok),
                            resources.getString(R.string.app__cancel)
                        ) {
                            dialog.cancel()

                            pDialog.show()

                            startSessionForPay(data.amount.toString(),transactionType)

                        }.show()

                    }

                    override fun onFailed(code: Int, msg: String?) {
                        pDialog.cancel()
                        dialog.showErrorDialogWithAction(
                            msg,
                            resources.getString(R.string.app__ok)
                        ) {
                            dialog.cancel()
                            if (code.toString() == Constants.CODE_UNAUTH || code.toString() == Constants.CODE_HTTP_UNAUTHORIZED) {
                                NavigateToActivity.navigateToMainActivity(this@AddBalance)
                            }

                        }.show()
                    }
                })
        }
    }

    private fun startSessionForPay(totalAmount: String,transactionType:String) {
        lifecycleScope.launch {
            xPayViewModel.startSessionForPay(
                payment_method_type = GatewayMethod.paytabs.toString(),
                transaction_type = transactionType,
                sharedHelper?.getUserToken()
                    .toString(),
                totalAmount,
                "69",
                object : OnResponseListener {
                    override fun onSuccess(code: Int, msg: String?, obj: Any?)
                    {
                        lifecycleScope.launch {
                            try {
                                val drawable =
                                    ContextCompat.getDrawable(
                                        this@AddBalance,
                                        R.drawable.new_logo_trans_small
                                    )

                                paytabsClick(
                                    transactionType,
                                    totalAmount = totalAmount,
                                    drawable
                                )

                            } catch (e: Exception) {

                                showFailedPay(e.message, code)
                            }
                        }
                    }

                    override fun onFailed(code: Int, msg: String?) {
                        showFailedPay(msg, code)
                    }
                })
        }

    }

    private fun showFailedPay(msg: String?, code: Int) {
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
    override fun onError(error: PaymentSdkError)
    {
        pDialog.cancel()

        val chargeBalanceRequest = ChargeBalanceRequestPaytabs(
            status = PaymentStatus.FAILED.toString(),
            id = Constants.START_SESSION_ID,
            amount = ui.amountValue.text.toString(),
            errorCode = error.code.toString(),
            errorMsg = error.msg,
            payment_method_type = GatewayMethod.paytabs.toString(),
            transaction_type = transactionTypeFinal
        )

        requestChargeFailed(chargeBalanceRequest,error.msg.toString())
    }

    override fun onPaymentCancel()
    {
        pDialog.cancel()

        val chargeBalanceRequest = ChargeBalanceRequestPaytabs(
            status = PaymentStatus.CANCELLED.toString(),
            id = Constants.START_SESSION_ID,
            amount = ui.amountValue.text.toString(),
            errorCode = "400",
            errorMsg = "Payment cancelled",
            payment_method_type = GatewayMethod.paytabs.toString(),
            transaction_type = transactionTypeFinal
        )

        requestChargeFailed(chargeBalanceRequest,"Payment cancelled")
    }

    override fun onPaymentFinish(paymentSdkTransactionDetails: PaymentSdkTransactionDetails) {
        val chargeBalanceRequest: ChargeBalanceRequestPaytabs

        if(paymentSdkTransactionDetails.isSuccess == true)
        {
            chargeBalanceRequest = ChargeBalanceRequestPaytabs(
                status = PaymentStatus.SUCCESSFUL.toString(),
                id = Constants.START_SESSION_ID,
                amount = ui.amountValue.text.toString(),
                card_id = paymentSdkTransactionDetails.cartID,
                total_amount = paymentSdkTransactionDetails.cartAmount,
                cartDescription = paymentSdkTransactionDetails.cartDescription,
                errorCode = paymentSdkTransactionDetails.errorCode,
                errorMsg = paymentSdkTransactionDetails.errorMsg,
                isAuthorized = paymentSdkTransactionDetails.isAuthorized,
                isOnHold = paymentSdkTransactionDetails.isOnHold,
                isPending = paymentSdkTransactionDetails.isPending,
                isProcessed = paymentSdkTransactionDetails.isProcessed,
                isSuccess = paymentSdkTransactionDetails.isSuccess,
                payResponseReturn = paymentSdkTransactionDetails.payResponseReturn,
                redirectUrl = paymentSdkTransactionDetails.redirectUrl,
                token = paymentSdkTransactionDetails.token,
                transactionReference = paymentSdkTransactionDetails.transactionReference,
                transactionType = paymentSdkTransactionDetails.transactionType,
                responseCode = paymentSdkTransactionDetails.paymentResult?.responseCode,
                responseMessage = paymentSdkTransactionDetails.paymentResult?.responseMessage,
                responseStatus = paymentSdkTransactionDetails.paymentResult?.responseStatus,
                transactionTime = paymentSdkTransactionDetails.paymentResult?.transactionTime,
                payment_method_type = GatewayMethod.paytabs.toString(),
                transaction_type = transactionTypeFinal
            )
        }
        else
        {
            chargeBalanceRequest = ChargeBalanceRequestPaytabs(
                status = PaymentStatus.FAILED.toString(),
                id = Constants.START_SESSION_ID,
                amount = ui.amountValue.text.toString(),
                card_id = paymentSdkTransactionDetails.cartID,
                total_amount = paymentSdkTransactionDetails.cartAmount,
                cartDescription = paymentSdkTransactionDetails.cartDescription,
                errorCode = paymentSdkTransactionDetails.errorCode,
                errorMsg = paymentSdkTransactionDetails.errorMsg,
                isAuthorized = paymentSdkTransactionDetails.isAuthorized,
                isOnHold = paymentSdkTransactionDetails.isOnHold,
                isPending = paymentSdkTransactionDetails.isPending,
                isProcessed = paymentSdkTransactionDetails.isProcessed,
                isSuccess = paymentSdkTransactionDetails.isSuccess,
                payResponseReturn = paymentSdkTransactionDetails.payResponseReturn,
                redirectUrl = paymentSdkTransactionDetails.redirectUrl,
                token = paymentSdkTransactionDetails.token,
                transactionReference = paymentSdkTransactionDetails.transactionReference,
                transactionType = paymentSdkTransactionDetails.transactionType,
                responseCode = paymentSdkTransactionDetails.paymentResult?.responseCode,
                responseMessage = paymentSdkTransactionDetails.paymentResult?.responseMessage,
                responseStatus = paymentSdkTransactionDetails.paymentResult?.responseStatus,
                transactionTime = paymentSdkTransactionDetails.paymentResult?.transactionTime,
                payment_method_type = GatewayMethod.paytabs.toString(),
                transaction_type = transactionTypeFinal
            )
        }

        requestToChargeBalance(chargeBalanceRequest)

    }

    private fun requestToChargeBalance(chargeBalanceRequest:ChargeBalanceRequestPaytabs) {
        lifecycleScope.launch {
            xPayViewModel.chargeBalanceWithPaytabs(sharedHelper?.getUserToken().toString(),
                chargeBalanceRequest,
                object : OnResponseListener {
                    override fun onSuccess(code: Int, msg: String?, obj: Any?) {
                        pDialog.cancel()

                        dialog.showSuccessDialog(
                            resources.getString(R.string.balance_added),
                            resources.getString(R.string.app__ok)
                        )
                        {
                            dialog.cancel()
                            finish()

                        }
                        dialog.show()

                    }

                    override fun onFailed(code: Int, msg: String?)
                    {
                        showFailedPay(msg, code)
                    }
                })
        }
    }

    private fun requestChargeFailed(chargeBalanceRequest:ChargeBalanceRequestPaytabs,
    errorMsg:String)
    {
        lifecycleScope.launch {
            xPayViewModel.chargeBalanceWithPaytabs(sharedHelper?.getUserToken().toString(),
                chargeBalanceRequest,
                object : OnResponseListener {
                    override fun onSuccess(code: Int, msg: String?, obj: Any?)
                    {
                        showFailedPay(errorMsg,code)
                    }

                    override fun onFailed(code: Int, msg: String?) {
                        showFailedPay(msg, code)
                    }
                })
        }
    }
}