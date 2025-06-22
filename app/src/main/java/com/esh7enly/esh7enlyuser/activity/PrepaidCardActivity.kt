package com.esh7enly.esh7enlyuser.activity

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.esh7enly.domain.entity.PaymentPojoModel
import com.esh7enly.domain.entity.TotalAmountPojoModel
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
import com.esh7enly.esh7enlyuser.util.Utils
import com.esh7enly.esh7enlyuser.util.sendIssueToCrashlytics
import com.payment.paymentsdk.PaymentSdkActivity
import com.payment.paymentsdk.integrationmodels.PaymentSdkError

import com.payment.paymentsdk.integrationmodels.PaymentSdkTransactionDetails
import com.payment.paymentsdk.sharedclasses.interfaces.CallbackPaymentInterface
import kotlinx.coroutines.launch

class PrepaidCardActivity : BaseActivity(), CallbackPaymentInterface {

    private val dialog by lazy {
        AppDialogMsg(this, false)
    }

    private var finalPaymentWay = ""
    private var transactionTypeFinal = ""
    private var amount: String? = null
    private var serviceCharge: String? = null
    private var paidAmount: String? = null
    private var totalAmountPojoModel: TotalAmountPojoModel? = null

    private val ui by lazy { ActivityPrepaidCardBinding.inflate(layoutInflater) }

    private var totalAmount = ""

    @SuppressLint("SetTextI18n")
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

            PayWays.CASH.toString() -> {
                pDialog.show()
                getTotalWithCash()
            }

            PayWays.BANk.toString(), PayWays.WALLET.toString()->{

                val way = if(
                    finalPaymentWay == PayWays.BANk.toString()) GatewayTransactionType.visa.toString() else  GatewayTransactionType.wallet.toString()

                dialog.showWarningDialogWithAction(
                    resources.getString(R.string.payment_warning),
                    resources.getString(R.string.app__ok)
                )
                {
                    dialog.cancel()

                    pDialog.show()

                    getTotalAmountForCharge(
                        transactionType = way,
                        amountForPay = totalAmount
                    )

                }.show()
            }

//            PayWays.WALLET.toString() -> {
//                dialog.showWarningDialogWithAction(
//                    resources.getString(R.string.payment_warning),
//                    resources.getString(R.string.app__ok)
//                )
//                {
//                    dialog.cancel()
//
//                    pDialog.show()
//
//                    getTotalAmountForCharge(
//                        transactionType = GatewayTransactionType.wallet.toString(),
//                        amountForPay = totalAmount
//                    )
//
//                }.show()
//            }
//            PayWays.BANk.toString() -> {
//
//                dialog.showWarningDialogWithAction(
//                    resources.getString(R.string.payment_warning),
//                    resources.getString(R.string.app__ok)
//                )
//                {
//                    dialog.cancel()
//
//                    pDialog.show()
//
//                    getTotalAmountForCharge(
//                        transactionType = GatewayTransactionType.visa.toString(),
//                        amountForPay = totalAmount
//                    )
//
//                }.show()
//
//            }

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
            serviceViewModel.pay(paymentPojoModel,
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

            serviceViewModel.getTotalAmount(
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

            paytabsViewModel.getTotalPay(
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
                            if (code == Constants.CODE_UNAUTHENTIC_NEW ||
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

        val configData = paytabsViewModel.generatePaytabsConfigurationDetails(
            serverKey = serverKey(),
            clientKey = clientKey(),
            secretKey = secretKey,
            transactionTitle = resources.getString(R.string.paytabs_title),
            value = totalAmount,
            drawable = drawable,
            startSessionId = startSessionId
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
        onChargeBalanceError(error = error, finalTotalAmount = totalAmount, transactionType = transactionTypeFinal)
    }

    override fun onPaymentCancel() {

        val paymentPojoModel = PaymentPojoModel(
            Constants.IMEI, "",
            totalAmountPojoModel?.serviceId!!, totalAmountPojoModel?.amount,
            totalAmountPojoModel?.attributes
        )

        onChargeBalanceCancelled(
            paymentPojoModel = paymentPojoModel,
            finalPaymentWay = finalPaymentWay,
            finalTotalAmount = totalAmount,
            transactionTypeFinal = transactionTypeFinal
        )
    }

    override fun onPaymentFinish(paymentSdkTransactionDetails: PaymentSdkTransactionDetails) {

        val paymentPojoModel = PaymentPojoModel(
            Constants.IMEI, "",
            totalAmountPojoModel?.serviceId!!,
            totalAmountPojoModel?.amount,
            totalAmountPojoModel?.attributes
        )

        onChargeBalanceSuccessful(
            paymentPojoModel = paymentPojoModel,
            paymentSdkTransactionDetails = paymentSdkTransactionDetails,
            transactionTypeFinal = transactionTypeFinal,
            totalAmount = totalAmount)

    }

}