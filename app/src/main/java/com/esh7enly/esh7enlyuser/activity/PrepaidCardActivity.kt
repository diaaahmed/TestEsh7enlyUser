package com.esh7enly.esh7enlyuser.activity

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.esh7enly.domain.entity.PaymentPojoModel
import com.esh7enly.domain.entity.TotalAmountPojoModel
import com.esh7enly.domain.entity.chargebalancerequest.ChargeBalanceRequestPaytabs
import com.esh7enly.domain.entity.totalamountxpayresponse.Data

import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.click.OnResponseListener
import com.esh7enly.esh7enlyuser.databinding.ActivityPrepaidCardBinding
import com.esh7enly.esh7enlyuser.util.AppDialogMsg
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.Constants.PROVIDER_NAME
import com.esh7enly.esh7enlyuser.util.Constants.SERVICE_AMOUNT
import com.esh7enly.esh7enlyuser.util.Constants.SERVICE_CHARGE
import com.esh7enly.esh7enlyuser.util.Constants.SERVICE_ICON
import com.esh7enly.esh7enlyuser.util.Constants.SERVICE_MODEL
import com.esh7enly.esh7enlyuser.util.Constants.SERVICE_NAME
import com.esh7enly.esh7enlyuser.util.Constants.SERVICE_PAID_AMOUNT
import com.esh7enly.esh7enlyuser.util.Constants.SERVICE_TOTAL_AMOUNT
import com.esh7enly.esh7enlyuser.util.GatewayMethod
import com.esh7enly.esh7enlyuser.util.GatewayTransactionType
import com.esh7enly.esh7enlyuser.util.Language
import com.esh7enly.esh7enlyuser.util.NavigateToActivity
import com.esh7enly.esh7enlyuser.util.NetworkUtils
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
import kotlinx.coroutines.launch

class PrepaidCardActivity : BaseActivity(), CallbackPaymentInterface {

    private val paytabsViewModel: PaytabsViewModel by viewModels()

    private val dialog by lazy {
        AppDialogMsg(this, false)
    }

    private var finalPaymentWay = ""
    private var transactionTypeFinal = ""
    private var amount: String? = null
    private var serviceCharge: String? = null
    private var paidAmount: String? = null
    private var totalAmountPojoModel: TotalAmountPojoModel? = null

    private val ui by lazy {
        ActivityPrepaidCardBinding.inflate(layoutInflater)
    }

    private var totalAmount = ""

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)

        ui.viewModel = paytabsViewModel
        ui.lifecycleOwner = this

        Language.setLanguageNew(this, Constants.LANG)

        amount = intent.getStringExtra(SERVICE_AMOUNT)

        totalAmountPojoModel = intent.extras?.getParcelable(
            SERVICE_MODEL,
            TotalAmountPojoModel::class.java
        )

        totalAmount = intent.getStringExtra(SERVICE_TOTAL_AMOUNT) ?: "Empty total amount"
        serviceCharge = intent.getStringExtra(SERVICE_CHARGE) ?: "Empty service charge"
        paidAmount = intent.getStringExtra(SERVICE_PAID_AMOUNT) ?: "Empty paid amount"

        ui.btnSubmit.text = resources.getString(R.string.pay)

        ui.btnSubmit.setOnClickListener { chargeBalance() }

        ui.tvAmount.text =
            Utils.format(amount.toString().toDouble()) + resources.getString(R.string.egp)

        ui.tvPaidAmou.text =
            Utils.format(paidAmount.toString().toDouble()) + resources.getString(R.string.egp)

        ui.tvTotalAmou.text =
            Utils.format(totalAmount.toDouble()) + resources.getString(R.string.egp)

        ui.tvFee.text =
            Utils.format(serviceCharge.toString().toDouble()) + resources.getString(R.string.egp)

        val serviceIcon = intent.getStringExtra(SERVICE_ICON)

        ui.tvProvider.text = intent.getStringExtra(PROVIDER_NAME) ?: "Empty provider"
        ui.tvService.text = intent.getStringExtra(SERVICE_NAME) ?: "Empty service"

        if (serviceIcon == null) {
            ui.img.setImageResource(R.drawable.new_logo_trans)
        } else {
            Utils.displayImageOriginalFromCache(
                this,
                ui.img,
                serviceIcon,
                NetworkUtils.isConnectedWifi(this)
            )
        }


        initToolBar()

        paytabsViewModel._buttonClicked.observe(this) { buttonClicked ->
            finalPaymentWay = buttonClicked
        }

        ui.bankWay.setOnClickListener {
            bankWayClicked()
        }

        ui.digitalWalletWay.setOnClickListener {
            cashWalletClicked()
        }

        ui.digitalWay.setOnClickListener {
            digitalWalletClicked()
        }

        ui.myWalletWay.setOnClickListener {
            myWalletClicked()
        }

    }

    private fun chargeBalance() {

        when (finalPaymentWay) {
            PayWays.WALLET.toString() -> {
                dialog.showWarningDialogWithAction(
                    resources.getString(R.string.payment_warning),
                    resources.getString(R.string.app__ok)
                )
                {
                    dialog.cancel()

                    pDialog.show()

                    getTotalAmountForCharge(
                        transactionType = GatewayTransactionType.wallet.toString(),
                        amountForPay = totalAmount
                    )

                }.show()
            }

            PayWays.CASH.toString() -> {
                pDialog.show()
                getTotalWithCash()
            }

            PayWays.BANk.toString() -> {

                dialog.showWarningDialogWithAction(
                    resources.getString(R.string.payment_warning),
                    resources.getString(R.string.app__ok)
                )
                {
                    dialog.cancel()

                    pDialog.show()

                    getTotalAmountForCharge(
                        transactionType = GatewayTransactionType.visa.toString(),
                        amountForPay = totalAmount
                    )

                }.show()

            }

            PayWays.Esh7enly.toString() -> {

                val paymentPojoModel = PaymentPojoModel(
                    Constants.IMEI, "",
                    totalAmountPojoModel?.serviceId!!, totalAmountPojoModel?.amount,
                    totalAmountPojoModel?.attributes
                )

                pay(paymentPojoModel)

            }
        }
    }

    private fun payWithCash(paymentPojoModel: PaymentPojoModel) {

        lifecycleScope.launch {
            serviceViewModel.pay(sharedHelper?.getUserToken().toString(), paymentPojoModel,
                object : OnResponseListener {
                    override fun onSuccess(code: Int, msg: String?, obj: Any?) {
                        pDialog.cancel()

                        val paymentPojoModel = PaymentPojoModel(
                            Constants.IMEI, "",
                            totalAmountPojoModel?.serviceId!!,
                            totalAmountPojoModel?.amount,
                            totalAmountPojoModel?.attributes
                        )

                        pay(paymentPojoModel)

                    }

                    override fun onFailed(code: Int, msg: String?) {
                        showFailedPay(msg, code)
                        Log.d("TAG", "diaa payWithCash error: $msg")
                    }
                })
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
                totalAmount, mutableListOf(params)
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
                                totalAmount,
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

    private fun getTotalAmountForCharge(transactionType: String, amountForPay: String) {

        // Amount for pay is balance in user wallet
        lifecycleScope.launch {

            paytabsViewModel.getTotalPay(sharedHelper?.getUserToken().toString(),
                paymentMethodType = GatewayMethod.paytabs.toString(),
                transactionType = transactionType,
                amount = amountForPay, object : OnResponseListener {
                    @RequiresApi(Build.VERSION_CODES.M)
                    override fun onSuccess(code: Int, msg: String?, obj: Any?) {

                        pDialog.cancel()

                        val data = obj as Data

                        // Amount is balance will in user wallet
                        val amount = java.lang.String.format(
                            " • %s %s %s",
                            getString(R.string.dialog_amount),
                            Utils.format(amountForPay.toDouble()),
                            getString(R.string.egp)
                        )
                        // Fees for paytabs
                        val fees = java.lang.String.format(
                            " • %s %s %s",
                            getString(R.string.service_fees),
                            Utils.format(data.fees),
                            getString(R.string.egp)
                        )

                        // The amount will take from user visa or wallet
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
                                amountForPay,
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
                                NavigateToActivity.navigateToAuthActivity(this@PrepaidCardActivity)
                            }

                        }.show()
                    }
                })
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun startSessionForPay(
        amount: String, totalAmount: String,
        transactionType: String
    ) {
        // Amount for user wallet
        // Total amount for paytabs

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
                    @RequiresApi(Build.VERSION_CODES.M)
                    override fun onSuccess(code: Int, msg: String?, obj: Any?) {
                        lifecycleScope.launch {
                            try {
                                val drawable =
                                    ContextCompat.getDrawable(
                                        this@PrepaidCardActivity,
                                        R.drawable.new_logo_trans_small
                                    )
                                Constants.TOTAL_AMOUNT_PAYTABS = totalAmount

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

    @RequiresApi(Build.VERSION_CODES.M)
    private fun paytabsClick(
        transactionType: String,
        totalAmount: String, drawable: Drawable?,
        startSessionId: Int
    ) {

        val configData: PaymentSdkConfigurationDetails =

            generatePaytabsConfigurationDetails(
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

    private external fun clientKey(): String
    private external fun serverKey(): String
    private external fun profileKey(): String
    private external fun aliasString(): String


    @RequiresApi(Build.VERSION_CODES.M)
    private fun generatePaytabsConfigurationDetails(
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

            sendIssueToCrashlytics(
                msg = e.message.toString(),
                functionName = "encryptedText AddBalance",
                key = "encryptedText AddBalance",
                provider = e.message.toString()
            )
        }

        val transactionTitle = resources.getString(R.string.paytabs_title)
        val cartDesc = "Add esh7enly balance" // Description in paytabs info
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
            .setCartId(Constants.HASH_ID)
            .setBillingData(billingData)
            .setShippingData(shippingData)
            .setScreenTitle(transactionTitle)

        return configData.build()
    }

    private fun showFailedPay(msg: String?, code: Int) {
        pDialog.cancel()

        dialog.showErrorDialogWithAction(
            msg, resources.getString(R.string.app__ok)
        ) {
            dialog.cancel()

            if (code == Constants.CODE_UNAUTH_NEW ||
                code.toString() == Constants.CODE_HTTP_UNAUTHORIZED
            ) {
                NavigateToActivity.navigateToAuthActivity(this@PrepaidCardActivity)
            }
        }.show()
    }

    private fun cashWalletClicked() {
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

    private fun myWalletClicked() {
        paytabsViewModel.setShowNumberNew(PayWays.Esh7enly.toString())
        paytabsViewModel.setShowNumber(false)
        paytabsViewModel.buttonClicked.value = PayWays.Esh7enly.toString()
        ui.lineWays.setBackgroundResource(R.drawable.payment_way_background)
    }

    private fun bankWayClicked() {
        paytabsViewModel.setShowNumber(false)
        paytabsViewModel.setShowNumberNew(PayWays.BANk.toString())
        paytabsViewModel.buttonClicked.value = PayWays.BANk.toString()
        ui.lineWays.setBackgroundResource(R.drawable.payment_way_background)
    }

    private fun initToolBar() {
        ui.inquireToolbar.title = intent.getStringExtra(PROVIDER_NAME) ?: "Empty provider"

        ui.inquireToolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    override fun onError(error: PaymentSdkError) {

        println("Diaa onError ")

        val chargeBalanceRequest = ChargeBalanceRequestPaytabs(
            status = PaymentStatus.FAILED.toString(),
            id = Constants.START_SESSION_ID,
            amount = totalAmount,
            errorCode = error.code.toString(),
            errorMsg = error.msg,
            payment_method_type = GatewayMethod.paytabs.toString(),
            transaction_type = transactionTypeFinal,
            hash_generated = Constants.HASH_GENERATED,
            hash_id = Constants.HASH_ID
        )

        requestChargeFailed(chargeBalanceRequest)
    }

    override fun onPaymentCancel() {

        println("Diaa onPaymentCancel ")

        if (finalPaymentWay == PayWays.WALLET.toString()) {
            // Call query by cart_id
            val chargeBalanceRequest = ChargeBalanceRequestPaytabs(
                status = PaymentStatus.CANCELLED_WALLET.toString(),
                id = Constants.START_SESSION_ID,
                amount = totalAmount,
                total_amount = Constants.TOTAL_AMOUNT_PAYTABS,
                payment_method_type = GatewayMethod.paytabs.toString(),
                transaction_type = transactionTypeFinal,
                hash_generated = Constants.HASH_GENERATED,
                hash_id = Constants.HASH_ID
            )

            requestChargeWalletCancelled(chargeBalanceRequest)

        } else {

            val chargeBalanceRequest = ChargeBalanceRequestPaytabs(
                status = PaymentStatus.CANCELLED.toString(),
                id = Constants.START_SESSION_ID,
                amount = totalAmount,
                errorCode = "400",
                errorMsg = "Payment cancelled",
                payment_method_type = GatewayMethod.paytabs.toString(),
                transaction_type = transactionTypeFinal,
                hash_generated = Constants.HASH_GENERATED,
                hash_id = Constants.HASH_ID
            )

            requestChargeFailed(chargeBalanceRequest)
        }
    }

    override fun onPaymentFinish(paymentSdkTransactionDetails: PaymentSdkTransactionDetails) {
        println("Diaa onPaymentFinish ${paymentSdkTransactionDetails.isSuccess}")

        var chargeBalanceRequest = ChargeBalanceRequestPaytabs(
            id = Constants.START_SESSION_ID,
            amount = totalAmount,
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

                        val paymentPojoModel = PaymentPojoModel(
                            Constants.IMEI, "",
                            totalAmountPojoModel?.serviceId!!,
                            totalAmountPojoModel?.amount,
                            totalAmountPojoModel?.attributes
                        )

                        pay(paymentPojoModel)

                    }

                    override fun onFailed(code: Int, msg: String?) {
                        showFailedPay(msg, code)
                    }
                })
        }
    }

    private fun requestChargeWalletCancelled(
        chargeBalanceRequest: ChargeBalanceRequestPaytabs,
    ) {
        lifecycleScope.launch {
            paytabsViewModel.checkWalletStatus(chargeBalanceRequest,
                object : OnResponseListener {
                    override fun onSuccess(code: Int, msg: String?, obj: Any?) {

                        pDialog.cancel()

                        val paymentPojoModel = PaymentPojoModel(
                            Constants.IMEI, "",
                            totalAmountPojoModel?.serviceId!!, totalAmountPojoModel?.amount,
                            totalAmountPojoModel?.attributes

                        )

                        pay(paymentPojoModel)

                    }

                    override fun onFailed(code: Int, msg: String?) {
                        showFailedPay(msg, code)
                    }
                })
        }
    }


    private fun requestChargeFailed(
        chargeBalanceRequest: ChargeBalanceRequestPaytabs,
    ) {
        lifecycleScope.launch {
            paytabsViewModel.chargeBalanceWithPaytabs(sharedHelper?.getUserToken().toString(),
                chargeBalanceRequest,
                object : OnResponseListener {
                    override fun onSuccess(code: Int, msg: String?, obj: Any?) {
                        pDialog.cancel()
                        showFailedPay(chargeBalanceRequest.errorMsg, code)
                    }

                    override fun onFailed(code: Int, msg: String?) {
                        pDialog.cancel()
                        showFailedPay(msg, code)
                    }
                })
        }
    }
}