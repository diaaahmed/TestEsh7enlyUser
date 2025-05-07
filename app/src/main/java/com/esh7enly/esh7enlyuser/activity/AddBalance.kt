package com.esh7enly.esh7enlyuser.activity

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.esh7enly.domain.entity.PaymentPojoModel
import com.esh7enly.domain.entity.TotalAmountPojoModel
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
import com.esh7enly.esh7enlyuser.util.Utils

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "AddBalance"

@AndroidEntryPoint
class AddBalance : BaseActivity(), IToolbarTitle {

    private val ui by lazy {
        ActivityAddBalanceBinding.inflate(layoutInflater)
    }

    private val dialog by lazy {
        AppDialogMsg(this, false)
    }

    private var finalPaymentWay = ""

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
                    if (ui.phoneNumber.text.toString().isEmpty()) {
                        ui.phoneNumber.error = resources.getString(R.string.required)
                    }
                    else
                    {
                        pDialog.show()

                        getTotalWithCash()
                    }

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


}