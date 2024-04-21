package com.esh7enly.esh7enlyuser.activity

import android.annotation.SuppressLint

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.esh7enly.domain.entity.TransactionDetailsEntity
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.databinding.ActivityTransactionDetailsBinding
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.Language
import com.esh7enly.esh7enlyuser.util.Screenshot
import com.esh7enly.esh7enlyuser.util.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


private const val TAG = "TransactionDetails"

@AndroidEntryPoint
class TransactionDetails : BaseActivity() {
    private val ui by lazy {
        ActivityTransactionDetailsBinding.inflate(
            layoutInflater
        )
    }

    private var type: Int? = null

    private var transactionId = ""

    private lateinit var view: View

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)

        Language.setLanguageNew(this, Constants.LANG)

        ui.serviceText.text = resources.getString(R.string.trans_details_service_title)
        ui.comissionText.text = resources.getString(R.string.trans_details_commission_title)
        ui.messageErrorText.text = resources.getString(R.string.trans_details_message_error_title)
        ui.tvProviderName.text = resources.getString(R.string.trans_details_provider_title)
        ui.createdAtText.text = resources.getString(R.string.trans_details_created_at_title)
        ui.amountText.text = resources.getString(R.string.trans_details_amount_title_new)
        ui.serialText.text = resources.getString(R.string.trans_details_serial_title)
        ui.clientNumberText.text = resources.getString(R.string.trans_details_client_number_title)
        ui.balanceAfterText.text = resources.getString(R.string.trans_details_balance_after_titlea)
        ui.balanceBeforeText.text = resources.getString(R.string.trans_details_balance_before_titlea)
        ui.totalAmountText.text = resources.getString(R.string.trans_details_total_amount_title_new)
        ui.transactionNumberText.text = resources.getString(R.string.trans_details_transaction_number_title)


        view = window.decorView.rootView

        getIntentData()

        loadData()

        ui.chatWhats.setOnClickListener {
            shareImage()
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun shareImage() {
        // Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
        val screenShootPath = Screenshot.takeScreenshot(this, view)
        Screenshot.share(this, screenShootPath)
    }


    private fun loadData() {
        transactionsViewModel.getTransactionDetails(
            sharedHelper?.getUserToken().toString(),
            transactionId
        )
        lifecycleScope.launch {
            transactionsViewModel.transactionDetails.collect {
                replaceData(it)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun replaceData(transaction: TransactionDetailsEntity?) {
        ui.tvCreatedAt.text = transaction?.data?.createdAt

        ui.tvTransactionNum.text = transaction?.data?.id.toString()
        ui.tvServiceName.text = transaction?.data?.service?.name + " : " +
                transaction?.data?.service?.provider?.name


        ui.tvProviderName.text = transaction?.data?.service?.provider?.name
        ui.tvAmount.text =
            Utils.format(transaction?.data?.amount?.toDouble()) + resources.getString(R.string.egp)
        ui.tvAmountHead.text =
            Utils.format(transaction?.data?.totalAmount?.toDouble()) + resources.getString(R.string.egp)
        ui.tvTotalAmount.text =
            Utils.format(transaction?.data?.totalAmount?.toDouble()) + resources.getString(R.string.egp)
        ui.tvBalanceBefore.text =
            Utils.format(transaction?.data?.balanceBefore?.toDouble()) + resources.getString(R.string.egp)
        ui.tvBalanceAfter.text =
            Utils.format(transaction?.data?.balanceAfter?.toDouble()) + resources.getString(R.string.egp)
        ui.tvCommision.text =
            Utils.format(transaction?.data?.merchantCommission?.toDouble()) + resources.getString(R.string.egp)


        if (transaction?.data?.isSettled === 0) {
            ui.lytCommision.visibility = View.GONE
        }

        if (transaction?.data?.clientNumber != null) {
            ui.lytClientNumber.visibility = View.VISIBLE
            ui.tvClientNumber.text = transaction.data.clientNumber
        }

        // Show prepaid card details
        if (transaction?.data?.service?.type == Constants.PREPAID_CARD)
        {
            if (transaction.data?.description != null && transaction.data?.description != "") {
                val lines =
                    transaction.data?.description?.split("\n")
                            as ArrayList<String>

                Log.d(TAG, "diaa lines one: $lines")
                for (oneLine in lines) {
                    if (oneLine.contains("PIN")) {
                        ui.lytPin.visibility = View.VISIBLE
                        ui.lytSerial.visibility = View.VISIBLE
                    } else {
                        val stringBuilder = StringBuilder()

                        for (i in lines.indices) {
                            val key = lines[i]
                            stringBuilder.append("• ")
                            stringBuilder.append(key)
                            if (i != lines.size - 1)
                                stringBuilder.append("\n")
                            ui.lytPin.visibility = View.VISIBLE
                            ui.tvPinTitle.visibility = View.GONE
                            ui.tvPin.text = stringBuilder.toString()
                        }
                    }
                }
            }
        }

        if (transaction?.data?.description != null && transaction.data?.description != "") {
            val likes: List<String>? = transaction.data?.description?.split("\n")

            likes?.let {
                for (oneLine in it) {
                    if (oneLine.contains("Serial")) {
                        ui.tvSerial.text = oneLine.trim().toLowerCase().replace("serial:", "")
                    } else if (oneLine.contains("PIN")) {
                        ui.tvPin.setText(oneLine.trim().toLowerCase().replace("pin:", ""))
                    }
                }
            }

        }

        type = transaction?.data?.status

        when (type) {
            Constants.DONE -> ui.tvTransactionStatus.text =
                resources.getString(R.string.print_succes)

            Constants.FAIL -> {
                ui.tvTransactionStatus.text = resources.getString(R.string.print_failed)
                ui.lytErrorMsg.visibility = View.VISIBLE
                ui.tvErrorMessage.text = transaction?.data?.message
                ui.lytBalanceAfter.visibility = View.GONE
                ui.lytBalanceBefore.visibility = View.GONE
                ui.lytCommision.visibility = View.GONE
            }

            Constants.PENDING -> ui.tvTransactionStatus.text =
                resources.getString(R.string.print_pending)
        }

    }

    private fun getIntentData() {
        serviceViewModel.transactionId =
            intent.getStringExtra(Constants.TRASACTION_ID).toString()
        serviceViewModel.transactionType = intent.getIntExtra(Constants.SERVICE_TYPE, 0)
        transactionId = intent.getStringExtra(Constants.TRASACTION_ID).toString()
    }
}