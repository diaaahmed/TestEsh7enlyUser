package com.esh7enly.esh7enlyuser.activity

import android.app.AlertDialog
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.esh7enly.data.sharedhelper.SharedHelper
import com.esh7enly.domain.entity.PaymentEntity
import com.esh7enly.domain.entity.PaymentPojoModel
import com.esh7enly.domain.entity.TotalAmountEntity
import com.esh7enly.domain.entity.TotalAmountPojoModel
import com.esh7enly.domain.entity.chargebalancerequest.ChargeBalanceRequestPaytabs
import com.esh7enly.esh7enlyuser.R


import com.esh7enly.esh7enlyuser.click.OnResponseListener
import com.esh7enly.esh7enlyuser.util.AppDialogMsg
import com.esh7enly.esh7enlyuser.util.Connectivity
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.Decryptor
import com.esh7enly.esh7enlyuser.util.Encryptor
import com.esh7enly.esh7enlyuser.util.GatewayMethod
import com.esh7enly.esh7enlyuser.util.NavigateToActivity
import com.esh7enly.esh7enlyuser.util.PayWays
import com.esh7enly.esh7enlyuser.util.PaymentStatus
import com.esh7enly.esh7enlyuser.viewModel.PaytabsViewModel
import com.esh7enly.esh7enlyuser.viewModel.ServiceViewModel
import com.esh7enly.esh7enlyuser.viewModel.TransactionsViewModel
import com.esh7enly.esh7enlyuser.viewModel.UserViewModel
import com.payment.paymentsdk.integrationmodels.PaymentSdkError
import com.payment.paymentsdk.integrationmodels.PaymentSdkTransactionDetails
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseActivity : AppCompatActivity() {

    val serviceViewModel: ServiceViewModel by viewModels()

    val paytabsViewModel: PaytabsViewModel by viewModels()

    val transactionsViewModel: TransactionsViewModel by viewModels()

    val userViewModel: UserViewModel by viewModels()

    var sharedHelper: SharedHelper? = null
        @Inject set

    var connectivity: Connectivity? = null
        @Inject set

    var encryptor: Encryptor? = null
        @Inject set

    var decryptor: Decryptor? = null
        @Inject set

    val pDialog by lazy {
        com.esh7enly.esh7enlyuser.util.ProgressDialog.createProgressDialog(this)
    }

    private val dialog by lazy {
        AppDialogMsg(this, false)
    }

    private var bulkNumber = 0

    private var isBulk = false

    open fun getTotalAmount(
        totalAmountPojoModel: TotalAmountPojoModel,
        serviceName: String = "",
        providerName: String = "",
        serviceIcon: String = "",
    ) {

        serviceViewModel.getTotalAmount(
            totalAmountPojoModel, object : OnResponseListener {
                override fun onSuccess(code: Int, msg: String?, obj: Any?) {

                    pDialog.cancel()

                    val data = obj as TotalAmountEntity.DataEntity

                    NavigateToActivity.navigateToParametersPayActivity(
                        activity = this@BaseActivity,
                        amount = data.amount.toString(),
                        totalAmount = data.totalAmount.toString(),
                        totalAmountPojoModel = totalAmountPojoModel,
                        serviceName = serviceName,
                        providerName = providerName,
                        serviceIcon = serviceIcon,
                        serviceCharge = data.serviceCharge.toString(),
                        paidAmount = data.paidAmount.toString(),
                    )
                }

                override fun onFailed(code: Int, msg: String?) {
                    pDialog.cancel()

                    dialog.showErrorDialogWithAction(
                        msg, resources.getString(R.string.app__ok)
                    ) {
                        dialog.cancel()

                        if (code == Constants.CODE_UNAUTHENTIC_NEW) {
                            lifecycleScope.launch(Dispatchers.Main)
                            {
                                NavigateToActivity.navigateToAuthActivity(this@BaseActivity)
                            }
                        }
                    }.show()
                }
            })
    }

    open lateinit var dataPay: PaymentEntity.DataEntity

    open fun pay(paymentPojoModel: PaymentPojoModel) {

        pDialog.show()

        serviceViewModel.pay(
            paymentPojoModel, object : OnResponseListener {
                override fun onSuccess(
                    code: Int,
                    msg: String?,
                    obj: Any?
                ) {
                    dataPay = obj as PaymentEntity.DataEntity

                    lifecycleScope.launch {
                        scheduleInquire(dataPay, paymentPojoModel)
                    }

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
                            lifecycleScope.launch(Dispatchers.Main)
                            {
                                NavigateToActivity.navigateToAuthActivity(this@BaseActivity)
                            }
                        }
                    }.show()
                }
            })
    }

    open fun scheduleInquire(
        result: PaymentEntity.DataEntity,
        paymentPojoModel: PaymentPojoModel
    ) {
        Log.d("TAG", "diaa scheduleInquire:start ")

        val clientNumber = result.clientNumber ?: "clientNumber"

        serviceViewModel.scheduleInquire(
            result.service.id.toString(), clientNumber,
            object : OnResponseListener {
                override fun onSuccess(code: Int, msg: String?, obj: Any?) {
                    //pDialog.cancel()
                    pDialog.cancel()

                    val builder = AlertDialog.Builder(this@BaseActivity)
                        .setMessage(resources.getString(R.string.add_to_reminder))
                        .setTitle(resources.getString(R.string.alert))
                        .setCancelable(false)
                        .setPositiveButton(resources.getString(R.string.add))
                        { alertDialog, _ ->
                            alertDialog.cancel()

                            val calendar = Calendar.getInstance()
                            val day = calendar.get(Calendar.DATE).toString()

                            pDialog.show()

                            lifecycleScope.launch {
                                scheduleInvoice(result, day, paymentPojoModel)
                            }
                        }
                        .setNegativeButton(resources.getString(R.string.no_add))
                        { alertDialog, _ ->
                            alertDialog.cancel()
                            if (isBulk) {
                                // Move to print with bulk
                                ReceiptActivity.getIntent(
                                    this@BaseActivity,
                                    isBulk, bulkNumber, result, paymentPojoModel,
                                    Constants.SERVICE_TYPE_TEST
                                )
                            } else {
                                // move to print without bulk
                                ReceiptActivity.getIntent(
                                    this@BaseActivity,
                                    result, Constants.SERVICE_TYPE_TEST

                                )
                            }
                        }

                    val alertDialog = builder.create()
                    alertDialog.show()
                }

                override fun onFailed(code: Int, msg: String?) {
                    pDialog.cancel()
                    // pDialog.cancel()

                    if (isBulk) {
                        // Move to print with bulk
                        ReceiptActivity.getIntent(
                            this@BaseActivity,
                            isBulk, bulkNumber, dataPay, paymentPojoModel,
                            serviceViewModel.serviceType
                        )
                    } else {
                        // move to print without bulk
                        ReceiptActivity.getIntent(
                            this@BaseActivity,
                            dataPay, serviceViewModel.serviceType
                        )
                    }
                }

            })
    }

    open fun scheduleInvoice(
        result: PaymentEntity.DataEntity,
        day: String,
        paymentPojoModel: PaymentPojoModel
    ) {
        Log.d("TAG", "diaa scheduleInvoice:start ")

        val clientNumber = result.clientNumber ?: "clientNumber"

        serviceViewModel.scheduleInvoice(
            result.service.id.toString(),
            day, clientNumber,
            object : OnResponseListener {
                override fun onSuccess(
                    code: Int,
                    msg: String?,
                    obj: Any?
                ) {
                    pDialog.cancel()
                    dialog.showSuccessDialog(msg, resources.getString(R.string.app__ok))
                    {
                        dialog.cancel()

                        if (isBulk) {
                            // Move to print with bulk
                            ReceiptActivity.getIntent(
                                this@BaseActivity,
                                isBulk, bulkNumber, result, paymentPojoModel,
                                serviceViewModel.serviceType
                            )
                        } else {
                            // move to print without bulk
                            ReceiptActivity.getIntent(
                                this@BaseActivity,
                                result, serviceViewModel.serviceType
                            )
                        }
                    }
                    dialog.show()
                }

                override fun onFailed(code: Int, msg: String?) {
                    pDialog.cancel()

                    dialog.showErrorDialogWithAction(
                        msg, resources.getString(R.string.app__ok)
                    ) {
                        dialog.cancel()

                        if (isBulk) {
                            // Move to print with bulk
                            ReceiptActivity.getIntent(
                                this@BaseActivity,
                                isBulk, bulkNumber, result, paymentPojoModel,
                                serviceViewModel.serviceType
                            )
                        } else {
                            // move to print without bulk
                            ReceiptActivity.getIntent(
                                this@BaseActivity,
                                result, serviceViewModel.serviceType
                            )
                        }

                    }.show()
                }

            })
    }

    fun requestChargeFailed(
        chargeBalanceRequest: ChargeBalanceRequestPaytabs,
    ) {
        lifecycleScope.launch {
            paytabsViewModel.chargeBalanceWithPaytabs(
                chargeBalanceRequest,
                object : OnResponseListener {
                    override fun onSuccess(code: Int, msg: String?, obj: Any?) {
                        pDialog.cancel()
                        showFailedPay(msg, code)
                    }

                    override fun onFailed(code: Int, msg: String?) {
                        pDialog.cancel()
                        showFailedPay(msg, code)
                    }
                })
        }

    }

    fun showFailedPay(msg: String?, code: Int) {
        pDialog.cancel()

        dialog.showErrorDialogWithAction(
            msg, resources.getString(R.string.app__ok)
        ) {
            dialog.cancel()

            if (code == Constants.CODE_UNAUTHENTIC_NEW ||
                code.toString() == Constants.CODE_HTTP_UNAUTHORIZED
            ) {
                NavigateToActivity.navigateToAuthActivity(this@BaseActivity)
            }
        }.show()
    }

    fun requestToChargeBalance(
        paymentPojoModel: PaymentPojoModel,
        chargeBalanceRequest: ChargeBalanceRequestPaytabs
    ) {
        lifecycleScope.launch {
            paytabsViewModel.chargeBalanceWithPaytabs(
                chargeBalanceRequest,
                object : OnResponseListener {
                    override fun onSuccess(code: Int, msg: String?, obj: Any?) {
                        pDialog.cancel()
                        pay(paymentPojoModel)

                    }

                    override fun onFailed(code: Int, msg: String?) {
                        showFailedPay(msg, code)
                    }
                })
        }
    }

    fun requestChargeWalletCancelled(
        paymentPojoModel: PaymentPojoModel,
        chargeBalanceRequest: ChargeBalanceRequestPaytabs,
    ) {
        lifecycleScope.launch {
            paytabsViewModel.checkWalletStatus(chargeBalanceRequest,
                object : OnResponseListener {
                    override fun onSuccess(code: Int, msg: String?, obj: Any?) {

                        pDialog.cancel()

                        pay(paymentPojoModel)

                    }

                    override fun onFailed(code: Int, msg: String?) {
                        showFailedPay(msg, code)
                    }
                })
        }
    }

    fun onChargeBalanceCancelled(
        paymentPojoModel: PaymentPojoModel,
        finalPaymentWay:String,
        finalTotalAmount:String,
        transactionTypeFinal:String
    ) {
        if (finalPaymentWay == PayWays.WALLET.toString()) {
            // Call query by cart_id
            val chargeBalanceRequest = ChargeBalanceRequestPaytabs(
                status = PaymentStatus.CANCELLED_WALLET.toString(),
                id = Constants.START_SESSION_ID,
                amount = finalTotalAmount,
                total_amount = Constants.TOTAL_AMOUNT_PAYTABS,
                payment_method_type = GatewayMethod.paytabs.toString(),
                transaction_type = transactionTypeFinal,
                hash_generated = Constants.HASH_GENERATED,
                hash_id = Constants.HASH_ID
            )

            requestChargeWalletCancelled(paymentPojoModel, chargeBalanceRequest)

        } else {
            val chargeBalanceRequest = ChargeBalanceRequestPaytabs(
                status = PaymentStatus.CANCELLED.toString(),
                id = Constants.START_SESSION_ID,
                amount = finalTotalAmount,
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

    fun onChargeBalanceError(
        error: PaymentSdkError, finalTotalAmount: String, transactionType: String
    ) {
        val chargeBalanceRequest = ChargeBalanceRequestPaytabs(
            status = PaymentStatus.FAILED.toString(),
            id = Constants.START_SESSION_ID,
            amount = finalTotalAmount,
            errorCode = error.code.toString(),
            errorMsg = error.msg,
            payment_method_type = GatewayMethod.paytabs.toString(),
            transaction_type = transactionType,
            hash_generated = Constants.HASH_GENERATED,
            hash_id = Constants.HASH_ID
        )
        requestChargeFailed(chargeBalanceRequest)

    }

    fun onChargeBalanceSuccessful(
        paymentPojoModel:PaymentPojoModel,
        paymentSdkTransactionDetails: PaymentSdkTransactionDetails,
        transactionTypeFinal:String,
        totalAmount:String)
    {
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

        requestToChargeBalance(
            paymentPojoModel = paymentPojoModel,
            chargeBalanceRequest = chargeBalanceRequest
        )
    }
}