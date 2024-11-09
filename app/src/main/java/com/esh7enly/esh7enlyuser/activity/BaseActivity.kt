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
import com.esh7enly.domain.entity.searchresponse.SearchData
import com.esh7enly.domain.entity.servicesNew.ServiceData
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.click.OnResponseListener
import com.esh7enly.esh7enlyuser.util.AppDialogMsg
import com.esh7enly.esh7enlyuser.util.Connectivity
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.Decryptor
import com.esh7enly.esh7enlyuser.util.Encryptor
import com.esh7enly.esh7enlyuser.util.NavigateToActivity
import com.esh7enly.esh7enlyuser.util.Utils
import com.esh7enly.esh7enlyuser.viewModel.ParametersViewModel
import com.esh7enly.esh7enlyuser.viewModel.PhoneViewModel
import com.esh7enly.esh7enlyuser.viewModel.ServiceViewModel
import com.esh7enly.esh7enlyuser.viewModel.TransactionsViewModel
import com.esh7enly.esh7enlyuser.viewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseActivity : AppCompatActivity()
{
    val serviceViewModel: ServiceViewModel by viewModels()
    val parametersViewModel: ParametersViewModel by viewModels()

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

    open fun getTotalAmount(totalAmountPojoModel: TotalAmountPojoModel)
    {

        serviceViewModel.getTotalAmount(sharedHelper?.getUserToken().toString(),
            totalAmountPojoModel, object : OnResponseListener {
                override fun onSuccess(code: Int, msg: String?, obj: Any?) {

                    val confirmation: String
                    val amount:String
                    val totalAmount: String
                    val ok: String
                    val cancel: String
                    val egp: String
                    val quantity: String

                    if (Constants.LANG == Constants.AR)
                    {
                        confirmation = "تأكيد"
                        amount = "المبلغ : "
                        totalAmount = "المبلغ الإجمالي : "
                        ok = "موافق"
                        cancel = "إلغاء"
                        egp = "ج.م"
                        quantity = "الكمية"

                    } else {
                        confirmation = "Confirmation"
                        amount = "Amount : "
                        totalAmount = "Total Amount : "
                        ok = "OK"
                        cancel = "Cancel"
                        egp = "EGP"
                        quantity = "quantity"
                    }

                    pDialog.cancel()

                    val data: TotalAmountEntity.DataEntity = obj as TotalAmountEntity.DataEntity

                    val paidAmount = ""

                    dialog.showSuccessDialogWithActionAndBulkCards(
                        confirmation,
                        " • " + amount +
                                " " + Utils.format(data.amount) + egp + " \n " +
                                "• " + totalAmount + " " +
                                Utils.format(data.totalAmount) + egp +
                                paidAmount,
                        ok,
                        cancel,
                        quantity
                    ) { quantity ->
                        dialog.cancel()

                        bulkNumber = Integer.parseInt(quantity)

                        isBulk = bulkNumber > 1

                        val paymentPojoModel = PaymentPojoModel(
                            Constants.IMEI, "",
                            totalAmountPojoModel.serviceId, totalAmountPojoModel.amount
                        )

                        pay(paymentPojoModel)

                    }.show()


                }

                override fun onFailed(code: Int, msg: String?) {
                    pDialog.cancel()

                    dialog.showErrorDialogWithAction(
                        msg, resources.getString(R.string.app__ok)
                    ) {
                        dialog.cancel()

                        if (code == Constants.CODE_UNAUTH_NEW) {
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

    private fun pay(paymentPojoModel: PaymentPojoModel) {

        pDialog.show()

        serviceViewModel.pay(sharedHelper?.getUserToken().toString(),
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

                        if (code == Constants.CODE_UNAUTH_NEW ||
                            code.toString() == Constants.CODE_HTTP_UNAUTHORIZED) {
                            lifecycleScope.launch(Dispatchers.Main)
                            {
                                NavigateToActivity.navigateToAuthActivity(this@BaseActivity)
                            }
                        }
                    }.show()
                }
            })
    }

    open fun scheduleInquire(result: PaymentEntity.DataEntity, paymentPojoModel: PaymentPojoModel) {
        Log.d("TAG", "diaa scheduleInquire:start ")

        val clientNumber = result.clientNumber ?: "clientNumber"

        serviceViewModel.scheduleInquire(sharedHelper?.getUserToken().toString(),
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

        serviceViewModel.scheduleInvoice(sharedHelper?.getUserToken().toString(),
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
                    dialog.show()
                }

                override fun onFailed(code: Int, msg: String?)
                {
                    pDialog.cancel()

                    dialog.showErrorDialogWithAction(
                        msg, resources.getString(R.string.app__ok)
                    ) {
                        dialog.cancel()

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

                    }.show()
                }

            })
    }


    fun navigateToParametersActivity(service: ServiceData) {
        val providerName =
            if (Constants.LANG == Constants.AR) {
                service.nameAr
            } else {
                service.nameEn
            }

        Log.d(
            "TAG",
            "diaa service from navigate: english  ${service.nameEn} arabic ${service.nameAr}"
        )
        NavigateToActivity.navigateToParametersActivity(
            this,
            service.type,
            providerName,
            service.id,
            service.nameAr,
            service.nameEn,
            service.acceptAmountInput,
            service.priceType,
            service.acceptCheckIntegrationProviderStatus,
            service.priceValue,
            service.acceptChangePaidAmount,
            service.icon,
            service.typeCode
        )
    }

    fun navigateToParametersActivity(service: SearchData) {
        val providerName =
            if (Constants.LANG == Constants.AR) {
                service.name_ar
            } else {
                service.name_en
            }

        NavigateToActivity.navigateToParametersActivity(
            this,
            service.type,
            providerName,
            service.id,
            service.name_ar,
            service.name_en,
            service.accept_amount_input,
            service.price_type,
            service.accept_check_integration_provider_status,
            service.price_value,
            service.accept_change_paid_amount,
            service.icon,
            service.type_code
        )
    }
}