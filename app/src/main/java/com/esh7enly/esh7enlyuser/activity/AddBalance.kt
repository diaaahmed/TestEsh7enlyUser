package com.esh7enly.esh7enlyuser.activity


import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.esh7enly.domain.entity.PaymentPojoModel
import com.esh7enly.domain.entity.TotalAmountPojoModel
import com.esh7enly.domain.entity.chargebalancerequest.ChargeBalanceRequestPaytabs
import com.esh7enly.domain.entity.totalamountxpayresponse.Data
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.click.OnResponseListener
import com.esh7enly.esh7enlyuser.databinding.ActivityAddBalanceBinding
import com.esh7enly.esh7enlyuser.util.AppDialogMsg
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.GatewayMethod
import com.esh7enly.esh7enlyuser.util.GatewayTransactionType
import com.esh7enly.esh7enlyuser.util.IToolbarTitle
import com.esh7enly.esh7enlyuser.util.Language
import com.esh7enly.esh7enlyuser.util.NavigateToActivity
import com.esh7enly.esh7enlyuser.util.PayWays
import com.esh7enly.esh7enlyuser.util.PaymentStatus
import com.esh7enly.esh7enlyuser.util.Utils
import com.esh7enly.esh7enlyuser.util.sendIssueToCrashlytics
import com.esh7enly.esh7enlyuser.viewModel.PaytabsViewModel
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
class AddBalance : BaseActivity(), IToolbarTitle, CallbackPaymentInterface {

    init {
        System.loadLibrary("esh7enlyuser")
    }

    private external fun clientKey(): String
    private external fun serverKey(): String
    private external fun profileKey(): String
    private external fun aliasString(): String

    private val ui by lazy {
        ActivityAddBalanceBinding.inflate(layoutInflater)
    }

    private val dialog by lazy {
        AppDialogMsg(this, false)
    }

    private var finalPaymentWay = ""
    private var transactionTypeFinal = ""

    private val paytabsViewModel: PaytabsViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)

        Language.setLanguageNew(this, Constants.LANG)

        ui.fillPrice.hint = resources.getString(R.string.required_amount)
        ui.fillPhoneNumber.hint = resources.getString(R.string.enter_phone_number)
        ui.bankWay.text = resources.getString(R.string.bank_card)
        ui.digitalWalletWay.text = resources.getString(R.string.vodafone_cash)
        ui.btnPay.text = resources.getString(R.string.pay)

        initToolBar()

        ui.viewModel = paytabsViewModel
        ui.lifecycleOwner = this

        paytabsViewModel._buttonClicked.observe(this) { buttonClicked ->
            finalPaymentWay = buttonClicked
        }

        ui.amountValue.addTextChangedListener { value ->

            if (value.toString().isBlank()) {
//                ui.serviceFee.text = ""
//                ui.totalAmount.text = ""
//                ui.serviceValue.text = ""
            } else {
                if (finalPaymentWay == PayWays.BANk.toString()) {
                    lifecycleScope.launch {
                        paytabsViewModel.amountNumber.emit(value.toString())
                    }
                }
            }
        }

        ui.bankWay.setOnClickListener {
            bankWayClicked()
        }

        ui.digitalWalletWay.setOnClickListener {
            cashWalletClicked()
        }

        ui.digitalWay.setOnClickListener{
            digitalWalletClicked()
        }

        ui.btnPay.setOnClickListener {

            payClicked()
        }

    }


    private fun cashWalletClicked() {
     //   paytabsViewModel.setShowNumberNew(PayWays.WALLET.toString())
        paytabsViewModel.setShowNumberNew(PayWays.CASH.toString())
        paytabsViewModel.setShowNumber(true)
        paytabsViewModel.buttonClicked.value = PayWays.CASH.toString()
        ui.lineWays.setBackgroundResource(R.drawable.payment_way_background)
    }

    private fun digitalWalletClicked() {
        paytabsViewModel.setShowNumberNew(PayWays.WALLET.toString())
        paytabsViewModel.setShowNumber(false)
        paytabsViewModel.buttonClicked.value = PayWays.WALLET.toString()
        ui.lineWays.setBackgroundResource(R.drawable.payment_way_background)
    }


    private fun bankWayClicked() {
        paytabsViewModel.setShowNumber(false)
        paytabsViewModel.setShowNumberNew(PayWays.BANk.toString())
        paytabsViewModel.buttonClicked.value = PayWays.BANk.toString()
        ui.lineWays.setBackgroundResource(R.drawable.payment_way_background)
    }

    private fun payClicked() {

        if (ui.amountValue.text.toString().isEmpty()) {
            ui.amountValue.error = resources.getString(R.string.required)
        } else {

            when (finalPaymentWay) {
                PayWays.BANk.toString() -> {

                    dialog.showWarningDialogWithAction(
                        resources.getString(R.string.payment_warning),
                        resources.getString(R.string.app__ok)
                    )
                    {
                        dialog.cancel()

                        pDialog.show()

                        getTotalAmount(GatewayTransactionType.visa.toString())

                    }.show()

                }

                PayWays.WALLET.toString() -> {
                    dialog.showWarningDialogWithAction(
                        resources.getString(R.string.payment_warning),
                        resources.getString(R.string.app__ok)
                    )
                    {
                        dialog.cancel()

                        pDialog.show()

                        getTotalAmount(GatewayTransactionType.wallet.toString())

                    }.show()
                }

                PayWays.CASH.toString() -> {
                    pDialog.show()

                    getTotalWithCash()

                }

                else -> {
                    Log.d(TAG, "diaa pay no way")

                }
            }

        }
    }


    private fun getTotalWithCash() {
        lifecycleScope.launch {

            val params =
                TotalAmountPojoModel.Params(
                    Constants.BILLING_ACCOUNT,
                    ui.phoneNumber.text.toString()
                )

            val totalAmountPojoModel = TotalAmountPojoModel(
                Constants.IMEI,
                Constants.VODAFONE_CASH_ID,
                ui.amountValue.text.toString(), mutableListOf(params)
            )

            serviceViewModel.getTotalAmount(sharedHelper?.getUserToken().toString(),
                totalAmountPojoModel,
                object : OnResponseListener {
                    override fun onSuccess(code: Int, msg: String?, obj: Any?) {

                        val paymentPojoModel =
                            PaymentPojoModel(
                                Constants.IMEI,
                                "",
                                Constants.VODAFONE_CASH_ID,
                                ui.amountValue.text.toString(),
                                mutableListOf(params)
                            )

                        payWithCash(paymentPojoModel)

                    }

                    override fun onFailed(code: Int, msg: String?) {
                        showFailedPay(msg, code)
                    }
                })
        }
    }

    private fun payWithCash(paymentPojoModel: PaymentPojoModel) {
        pDialog.show()

        lifecycleScope.launch {
            serviceViewModel.pay(sharedHelper?.getUserToken().toString(), paymentPojoModel,
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

                    override fun onFailed(code: Int, msg: String?) {
                        showFailedPay(msg, code)
                        Log.d(TAG, "diaa payWithCash error: $msg")
                    }
                })
        }
    }

    private fun paytabsClick(
        transactionType: String,
        totalAmount: String, drawable: Drawable?,
        startSessionId: Int
    ) {
       // val number = Random(9000000000000000000).nextInt()
        val number = "cart_diaa_wallet_test"

        Log.d(TAG, "diaa wallet click number: $number ")

        val configData: PaymentSdkConfigurationDetails =

            generatePaytabsConfigurationDetails(
                number.toString(),
                totalAmount, drawable, startSessionId
            )

        when (transactionType) {
            GatewayTransactionType.visa.toString() -> {
                transactionTypeFinal = GatewayTransactionType.visa.toString()

                PaymentSdkActivity.startCardPayment(this, configData, this)
            }

            GatewayTransactionType.wallet.toString() -> {
                transactionTypeFinal = GatewayTransactionType.wallet.toString()

                PaymentSdkActivity.startAlternativePaymentMethods(this, configData, this)
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun generatePaytabsConfigurationDetails(
        orderNum: String,
        value: String,
        drawable: Drawable?,
        startSessionId: Int
    ): PaymentSdkConfigurationDetails {

        var secretKey: String? = null

        try {
            val encryptedText = encryptor?.encryptText(aliasString(), profileKey())
            Base64.encodeToString(encryptedText, Base64.DEFAULT)

            secretKey = decryptor?.decryptData(
                aliasString(), encryptor?.encryption, encryptor?.iv
            )

        } catch (e: Exception) {

            Log.d(TAG, "diaa first exception: ${e.message}")
            sendIssueToCrashlytics(
                msg = e.message.toString(),
                functionName = "encryptedText AddBalance",
                key = "encryptedText AddBalance",
                provider = e.message.toString()
            )
        }

        val transactionTitle = resources.getString(R.string.paytabs_title)
        val cartDesc = "Add esh7enly balance" // Description in paytab info
        val currency = "EGP"
        val merchantCountryCode = "EG"
        val amount: Double = value.toDouble()

        val locale = PaymentSdkLanguageCode.AR

        val billingData = PaymentSdkBillingDetails(
            "City",
            countryCode = merchantCountryCode,
            email = sharedHelper?.getUserEmail().toString(),
            name = sharedHelper?.getStoreName().toString(),
            phone = sharedHelper?.getUserPhone().toString(),
            state = "zipcode",
            addressLine = "Egypt",
            zip = ""
        )

        // Customer details
        val shippingData = PaymentSdkShippingDetails(
            "City",
            countryCode = merchantCountryCode,
            email = sharedHelper?.getUserEmail().toString(),
            name = "${sharedHelper?.getStoreName().toString()} , $startSessionId",
            phone = sharedHelper?.getUserPhone().toString(),
            state = "zipcode",
            addressLine = "Egypt",
            zip = ""
        )

        val configData = PaymentSdkConfigBuilder(
            secretKey!!, serverKey(), clientKey(), amount, currency
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

    private fun getTotalAmount(transactionType: String) {
        lifecycleScope.launch {

            paytabsViewModel.getTotalPay(sharedHelper?.getUserToken().toString(),
                paymentMethodType = GatewayMethod.paytabs.toString(),
                transactionType = transactionType,
                ui.amountValue.text.toString(), object : OnResponseListener {
                    override fun onSuccess(code: Int, msg: String?, obj: Any?) {
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

                            startSessionForPay(
                                ui.amountValue.text.toString(),
                                data.amount.toString(), transactionType
                            )

                        }.show()
                    }

                    override fun onFailed(code: Int, msg: String?) {
                        pDialog.cancel()

                        dialog.showErrorDialogWithAction(
                            msg,
                            resources.getString(R.string.app__ok)
                        ) {
                            dialog.cancel()
                            if (code == Constants.CODE_UNAUTH_NEW ||
                                code.toString() == Constants.CODE_HTTP_UNAUTHORIZED
                            ) {
                                NavigateToActivity.navigateToAuthActivity(this@AddBalance)
                            }

                        }.show()
                    }
                })
        }
    }

    private fun startSessionForPay(
        amount: String, totalAmount: String,
        transactionType: String
    ) {
        lifecycleScope.launch {

            paytabsViewModel.startSessionForPay(
                finalAmount = amount,
                paymentMethodType = GatewayMethod.paytabs.toString(),
                transactionType = transactionType,
                sharedHelper?.getUserToken()
                    .toString(),
                totalAmount,
                "69",
                object : OnResponseListener {
                    override fun onSuccess(code: Int, msg: String?, obj: Any?) {
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
                                    drawable,
                                    Constants.START_SESSION_ID
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

    private fun showFailedPay(msg: String?, code: Int)
    {
        pDialog.cancel()

        dialog.showErrorDialogWithAction(
            msg, resources.getString(R.string.app__ok)
        ) {
            dialog.cancel()

            if (code == Constants.CODE_UNAUTH_NEW ||
                code.toString() == Constants.CODE_HTTP_UNAUTHORIZED
            ) {
                NavigateToActivity.navigateToAuthActivity(this@AddBalance)
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
            transaction_type = transactionTypeFinal,
            hash_generated = Constants.HASH_GENERATED,
            hash_id = Constants.HASH_ID
        )


        requestChargeFailed(chargeBalanceRequest, error.msg.toString())
    }

    override fun onPaymentCancel()
    {
        pDialog.cancel()

        if(finalPaymentWay== PayWays.WALLET.toString())
        {
            // Call query by cart_id
        }
        else
        {
            val chargeBalanceRequest = ChargeBalanceRequestPaytabs(
                status = PaymentStatus.CANCELLED.toString(),
                id = Constants.START_SESSION_ID,
                amount = ui.amountValue.text.toString(),
                errorCode = "400",
                errorMsg = "Payment cancelled",
                payment_method_type = GatewayMethod.paytabs.toString(),
                transaction_type = transactionTypeFinal,
                hash_generated = Constants.HASH_GENERATED,
                hash_id = Constants.HASH_ID
            )

            requestChargeFailed(chargeBalanceRequest, "Payment cancelled")
        }


    }

    override fun onPaymentFinish(
        paymentSdkTransactionDetails: PaymentSdkTransactionDetails
    )
    {
        var chargeBalanceRequest = ChargeBalanceRequestPaytabs(
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
            transaction_type = transactionTypeFinal,
            hash_generated = Constants.HASH_GENERATED,
            hash_id = Constants.HASH_ID
        )

        chargeBalanceRequest = if (paymentSdkTransactionDetails.isSuccess == true) {
            chargeBalanceRequest.copy(
                status = PaymentStatus.SUCCESSFUL.toString()
            )
        } else {
            chargeBalanceRequest.copy(
                status = PaymentStatus.FAILED.toString()
            )
        }

        requestToChargeBalance(chargeBalanceRequest)

    }

    private fun requestToChargeBalance(chargeBalanceRequest: ChargeBalanceRequestPaytabs) {
        lifecycleScope.launch {
            paytabsViewModel.chargeBalanceWithPaytabs(
                sharedHelper?.getUserToken().toString(),
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

                    override fun onFailed(code: Int, msg: String?) {
                        showFailedPay(msg, code)
                    }
                })
        }
    }

    private fun requestChargeFailed(
        chargeBalanceRequest: ChargeBalanceRequestPaytabs,
        errorMsg: String
    ) {
        lifecycleScope.launch {
            paytabsViewModel.chargeBalanceWithPaytabs(sharedHelper?.getUserToken().toString(),
                chargeBalanceRequest,
                object : OnResponseListener {
                    override fun onSuccess(code: Int, msg: String?, obj: Any?) {
                        showFailedPay(errorMsg, code)
                    }

                    override fun onFailed(code: Int, msg: String?) {
                        showFailedPay(msg, code)
                    }
                })
        }
    }
}