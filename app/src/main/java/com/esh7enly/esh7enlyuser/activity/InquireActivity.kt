package com.esh7enly.esh7enlyuser.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.InputType
import android.util.Base64
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.esh7enly.domain.entity.FawryEntity
import com.esh7enly.domain.entity.PaymentEntity
import com.esh7enly.domain.entity.PaymentPojoModel
import com.esh7enly.domain.entity.SpinnerModel
import com.esh7enly.domain.entity.TotalAmountEntity
import com.esh7enly.domain.entity.TotalAmountPojoModel
import com.esh7enly.domain.entity.chargebalancerequest.ChargeBalanceRequestPaytabs
import com.esh7enly.domain.entity.parametersNew.ParametersData
import com.esh7enly.domain.entity.totalamountxpayresponse.Data
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.click.OnResponseListener
import com.esh7enly.esh7enlyuser.databinding.ActivityInquireBinding
import com.esh7enly.esh7enlyuser.util.AppDialogMsg
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.DynamicLayout
import com.esh7enly.esh7enlyuser.util.GatewayMethod
import com.esh7enly.esh7enlyuser.util.GatewayTransactionType
import com.esh7enly.esh7enlyuser.util.Language
import com.esh7enly.esh7enlyuser.util.NavigateToActivity
import com.esh7enly.esh7enlyuser.util.NetworkUtils
import com.esh7enly.esh7enlyuser.util.PayWays
import com.esh7enly.esh7enlyuser.util.PaymentStatus
import com.esh7enly.esh7enlyuser.util.PermissionHelper
import com.esh7enly.esh7enlyuser.util.ServicesCard
import com.esh7enly.esh7enlyuser.util.TimeDialogs
import com.esh7enly.esh7enlyuser.util.Utils
import com.esh7enly.esh7enlyuser.util.sendIssueToCrashlytics
import com.fawry.nfc.NFC.Main.NFCFawry
import com.fawry.nfc.NFC.interfaces.NFCWriteCallback
import com.fawry.nfc.NFC.models.FawryWapperStatus
import com.fawry.nfc.NFC.models.FawryWrapperResponse
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import java.util.Objects
import javax.inject.Inject

private const val TAG = "InquireActivity"

@AndroidEntryPoint
class InquireActivity : BaseActivity(), CallbackPaymentInterface {

    companion object {

        private var NAME_AR: String? = null
        private var DATA_ENTITY: PaymentEntity.DataEntity? = null
        private var PAYMENTPOJOMODEL: PaymentPojoModel? = null
        private var SERVICE_TYPE = 0
        private var SERVICE_AMOUNT_INPUT = 0
        private var NAME_EN: String? = null
        private var IMAGE: String? = null
        private var PROVIDER_NAME: String? = null
        private var SERVICE_ID = 0
        private var ACCEPT_CHECK_INTEGRATION_PROVIDER_STATUS = 0
        private var ACCEPT_AMOUNT_CHANGE = 0
        private var SERVICE_TYPE_CODE: String? = null

        private var finalAmount = ""
        private var finalTotalAmount = ""

        fun getIntent(
            serviceId: Int, serviceType: Int,
            acceptCheckIntegrationProviderStatus: Int, image: String,
            acceptAmountChange: Int, nameAr: String, nameEn: String, providerName: String,
            data: PaymentEntity.DataEntity, paymentPojoModel: PaymentPojoModel,
            serviceTypeCode: String, activity: Activity, serviceAmountInput: Int
        ) {

            try {
                val intent = Intent(activity, InquireActivity::class.java)
                NAME_AR = nameAr
                NAME_EN = nameEn
                IMAGE = image
                PROVIDER_NAME = providerName
                SERVICE_ID = serviceId
                SERVICE_TYPE = serviceType
                ACCEPT_AMOUNT_CHANGE = acceptAmountChange
                ACCEPT_CHECK_INTEGRATION_PROVIDER_STATUS = acceptCheckIntegrationProviderStatus
                SERVICE_TYPE_CODE = serviceTypeCode
                PAYMENTPOJOMODEL = paymentPojoModel
                DATA_ENTITY = data
                SERVICE_AMOUNT_INPUT = serviceAmountInput

                activity.startActivity(intent)
            } catch (e: Exception) {
                sendIssueToCrashlytics(
                    key = "InquireActivity getIntent",
                    provider = "InquireActivity getIntent",
                    msg = e.message.toString(),
                    functionName = "Get intent"
                )
            }
        }
    }

    //  private val serviceViewModel: ServiceViewModel by viewModels()

    private val REQUESTCODE = 100
    private var internalId: String? = null

    private var editedAmount = ""

//    private val pDialog by lazy {
//        ProgressDialog.createProgressDialog(this)
//    }

    private var parametersList: List<ParametersData> = emptyList()

    var dynamicLayout: DynamicLayout? = null
        @Inject set

//    var sharedHelper: SharedHelper? = null
//        @Inject set
//
//    var connectivity: Connectivity? = null
//        @Inject set

    private val dialog by lazy {
        AppDialogMsg(this, false)
    }

    private val lang by lazy {
        Constants.LANG
    }

    private var paramsArrayListToSend = arrayListOf<TotalAmountPojoModel.Params>()

    private val ui by lazy {
        ActivityInquireBinding.inflate(layoutInflater)
    }

    private var finalPaymentWay = ""
    private var transactionTypeFinal = ""

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

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)

        Language.setLanguageNew(this, Constants.LANG)

        ui.viewModel = paytabsViewModel
        ui.lifecycleOwner = this

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

        initToolBar()

        // getIntentData()

        ui.btnSubmit.text = resources.getString(R.string.pay)

        ui.btnSubmit.setOnClickListener { submitBtn() }

        ui.tvAmount.text = Utils.format(DATA_ENTITY?.amount) + resources.getString(R.string.egp)

        ui.tvPaidAmou.text =
            Utils.format(DATA_ENTITY?.paidAmount) + resources.getString(R.string.egp)

        ui.tvTotalAmou.text =
            Utils.format(DATA_ENTITY?.totalAmount) + resources.getString(R.string.egp)
        ui.tvFee.text = Utils.format(DATA_ENTITY?.serviceCharge) + resources.getString(R.string.egp)

        ui.tvProvider.text = PROVIDER_NAME
        ui.tvService.text = " $NAME_AR"

        if (DATA_ENTITY?.description != null && DATA_ENTITY?.description != "") {
            ui.lytInformation.visibility = View.VISIBLE
            ui.tvInfo.text = DATA_ENTITY?.description
        }

        if (ACCEPT_AMOUNT_CHANGE == 1 && DATA_ENTITY?.maxAmount != DATA_ENTITY?.minAmount) {
            ui.tvAmountIsEditable.visibility = View.VISIBLE
            ui.lytAmount.setOnClickListener {
                showEditAmountDialog()
            }
        }

        if (IMAGE == null) {
            ui.img.setImageResource(R.drawable.new_logo_trans)
        } else {
            Utils.displayImageOriginalFromCache(
                this,
                ui.img,
                IMAGE,
                NetworkUtils.isConnectedWifi(this)
            )
        }

        val stringBuilder: StringBuilder = StringBuilder()

        for (i in DATA_ENTITY?.parameters?.indices!!) {
            val key = DATA_ENTITY?.parameters!![i].key
            val value = DATA_ENTITY?.parameters!![i].value
            stringBuilder.append("•")
            stringBuilder.append(key)
            stringBuilder.append(" : ")
            stringBuilder.append(value)
            if (i != DATA_ENTITY!!.parameters.size - 1) {
                stringBuilder.append("\n")
            }

            ui.tvParams.text = stringBuilder.toString()
        }

        getParametersRemotely()
    }

    private fun myWalletClicked() {
        paytabsViewModel.setShowNumberNew(PayWays.Esh7enly.toString())
        paytabsViewModel.setShowNumber(false)
        paytabsViewModel.buttonClicked.value = PayWays.Esh7enly.toString()
        ui.lineWays.setBackgroundResource(R.drawable.payment_way_background)
    }

    private fun getParametersRemotely() {
        pDialog.show()

        serviceViewModel.getParametersNew(
            SERVICE_ID.toString(),
            object : OnResponseListener {
                override fun onSuccess(code: Int, msg: String?, obj: Any?) {
                    pDialog.cancel()
                    val parameters = obj as List<ParametersData>

                    replaceData(parameters)
                }

                override fun onFailed(code: Int, msg: String?) {
                    pDialog.cancel()

                    Log.d("TAG", "diaa onFailed: $msg")

                    dialog.showErrorDialogWithAction(
                        msg, resources.getString(R.string.app__ok)
                    ) {
                        dialog.cancel()

                        if (code == Constants.CODE_UNAUTH_NEW ||
                            code.toString() == Constants.CODE_HTTP_UNAUTHORIZED
                        ) {
                            NavigateToActivity.navigateToAuthActivity(this@InquireActivity)
                        }
                    }.show()
                }
            })
    }

    private fun initToolBar() {
        ui.inquireToolbar.title = PROVIDER_NAME

        ui.inquireToolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun replaceData(parameters: List<ParametersData>) {
        val cardParameters = arrayListOf<String>()

        cardParameters.add("ClientIdentifier")
        cardParameters.add("billing_account")
        cardParameters.add("ElectrictyCompany")
        cardParameters.add("WaterCompany")
        cardParameters.add("GasCompany")
        cardParameters.add("CardMetadata")
        cardParameters.add("Key1")
        cardParameters.add("Key2")
        cardParameters.add("Key3")
        cardParameters.add("ESCFBT01")
        cardParameters.add("ESCFBT02")
        cardParameters.add("ESCFBT03")

        ui.lytDynamic.removeAllViewsInLayout()

        this.parametersList = parameters

        for (i in this.parametersList.indices) {
            val internalId: String = this.parametersList[i].internal_id
            //  val serviceId: Int = this.parametersList[i].service_id
            val type: Int = this.parametersList[i].type
            Log.d(TAG, "diaa replace data type: $type")
            val paramNameAr: String = this.parametersList[i].name_ar
            val paramNameEn: String = this.parametersList[i].name_en

            // val minLenght: Int = this.parametersList[i].min_length
            //val maxLenght: Int = this.parametersList[i].max_length
            //val required: Int = this.parametersList[i].required
            val isClientNum: Int = this.parametersList[i].is_client_number

            val display: String = this.parametersList[i].display.toString()

            //val selectedValue = ""
            val paramName: String = if (lang == Constants.AR) {
                paramNameAr
            } else {
                paramNameEn

            }
            val values: ArrayList<SpinnerModel> = ArrayList<SpinnerModel>()

            for (ii in 0 until parametersList[i].type_values.size) {
                values.add(
                    SpinnerModel(
                        java.lang.String.valueOf(
                            parametersList[i].type_values[ii].value
                        ),
                        parametersList[i].type_values[ii].name_ar,
                        parametersList[i].type_values[ii].name_en
                    )
                )
            }

            if (display == Constants.DISPLAY_FOR_ALL ||
                display == Constants.DISPLAY_FOR_PAYMENT
            ) {
                when (type) {
                    Constants.Number -> {
                        // adding title
                        dynamicLayout?.addTextViews(
                            ui.lytDynamic,  /*"\u2022 " +*/
                            paramName,
                            ContextCompat.getColor(this, R.color.colorPrimary)
                            // resources.getColor(R.color.colorPrimary)
                        )
                        // adding view
                        dynamicLayout?.addViews(ui.lytDynamic, 0, 0, 2)
                        // adding edit text
                        dynamicLayout?.addEditTexts(
                            ui.lytDynamic,
                            true,
                            internalId,
                            paramName,
                            InputType.TYPE_CLASS_NUMBER,
                            1
                        ) { }

                        val edtNumber: EditText =
                            ui.lytDynamic.findViewWithTag(internalId)

                        edtNumber.setText("")

                        setValue(internalId, edtNumber)

                        if (isClientNum == 1) {
                            if (lang.equals(Constants.AR)) {
                                edtNumber.setCompoundDrawablesWithIntrinsicBounds(
                                    R.drawable.ic_profile,
                                    0,
                                    0,
                                    0
                                )
                            } else {
                                edtNumber.setCompoundDrawablesWithIntrinsicBounds(
                                    0,
                                    0,
                                    R.drawable.ic_profile,
                                    0
                                )
                            }
                            edtNumber.compoundDrawablePadding = 24
                            edtNumber.setOnTouchListener { _: View?, event: MotionEvent ->
                                val DRAWABLE_LEFT = 0
                                // val DRAWABLE_TOP = 1
                                val DRAWABLE_RIGHT = 2
                                //  val DRAWABLE_BOTTOM = 3
                                if (event.action == MotionEvent.ACTION_UP) {
                                    if (lang.equals(Constants.AR)) {
                                        if (event.rawX <=
                                            edtNumber.compoundDrawables[DRAWABLE_LEFT].bounds.width() * 3
                                        ) {
                                            // your action here
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                val result: Boolean =
                                                    PermissionHelper.checkSinglePermission(
                                                        this,
                                                        Manifest.permission.READ_CONTACTS,
                                                        REQUESTCODE
                                                    )
                                                if (result) {
                                                    startActivity(internalId)
                                                }
                                            } else {
                                                startActivity(internalId)
                                            }
                                            return@setOnTouchListener true
                                        }
                                    } else {
                                        if (event.rawX >= edtNumber.right - edtNumber.compoundDrawables[DRAWABLE_RIGHT].bounds.width() * 2
                                        ) {
                                            // your action here
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                val result: Boolean =
                                                    PermissionHelper.checkSinglePermission(
                                                        this,
                                                        Manifest.permission.READ_CONTACTS,
                                                        REQUESTCODE
                                                    )
                                                if (result) {
                                                    startActivity(internalId)
                                                }
                                            } else {
                                                startActivity(internalId)
                                            }
                                            return@setOnTouchListener true
                                        }
                                    }
                                }
                                false
                            }
                        }
                        edtNumber.visibility =
                            if (cardParameters.contains(internalId)) View.GONE else View.VISIBLE
                    }

                    Constants.Char -> {
                        // adding title
//                        dynamicLayout?.addTextViews(
//                            ui.lytDynamic,  /*"\u2022 " +*/
//                            paramName
//                        )
                        // adding view
                        //        dynamicLayout?.addViews(ui.lytDynamic, 0, 0, 2)
                        // adding edit text
                        dynamicLayout?.addEditTexts(
                            ui.lytDynamic,
                            true,
                            internalId,
                            paramName,
                            InputType.TYPE_CLASS_TEXT,
                            1
                        ) { }
                        val edtChar: EditText = ui.lytDynamic.findViewWithTag(internalId)
                        edtChar.setText("")
                        setValue(internalId, edtChar)
                        edtChar.visibility =
                            if (cardParameters.contains(internalId)) View.GONE else View.VISIBLE
                    }

                    Constants.Date -> {
                        // adding title
                        dynamicLayout?.addTextViews(
                            ui.lytDynamic,  /*"\u2022 " +*/
                            paramName
                        )
                        // adding view
                        dynamicLayout?.addViews(ui.lytDynamic, 0, 0, 2)
                        // adding edit text
                        dynamicLayout?.addEditTexts(
                            ui.lytDynamic,
                            false,
                            internalId,
                            paramName,
                            InputType.TYPE_CLASS_TEXT,
                            1
                        ) { }

                        // get created dynamic editText by id
                        val etDateTime: EditText =
                            ui.lytDynamic.findViewWithTag(internalId)
                        etDateTime.setOnClickListener {
                            TimeDialogs.openDateCalender(
                                this,
                                etDateTime
                            )
                        }

                        setValue(internalId, etDateTime)
                        etDateTime.visibility =
                            if (cardParameters.contains(internalId)) View.GONE else View.VISIBLE
                    }

                    Constants.textarea -> {
                        // adding title
                        dynamicLayout?.addTextViews(
                            ui.lytDynamic,  /*"\u2022 " +*/
                            paramName
                        )
                        // adding view
                        dynamicLayout?.addViews(ui.lytDynamic, 0, 0, 2)
                        // adding edit text
                        dynamicLayout?.addEditTexts(
                            ui.lytDynamic,
                            true,
                            internalId,
                            paramName,
                            InputType.TYPE_CLASS_TEXT,
                            4
                        ) { }
                        val edtTxtArea: EditText =
                            ui.lytDynamic.findViewWithTag(internalId)
                        edtTxtArea.setText("")
                        setValue(internalId, edtTxtArea)
                        edtTxtArea.visibility =
                            if (cardParameters.contains(internalId)) View.GONE else View.VISIBLE
                    }

                    Constants.Select -> {
                        //add header item in spinner list
                        values.add(SpinnerModel("0", "أختر $paramNameAr", "Choose$paramNameAr"))
                        dynamicLayout?.addTextViews(
                            ui.lytDynamic,  /*"\u2022 " +*/
                            "$paramName "
                        )
                        // adding view
                        dynamicLayout?.addViews(ui.lytDynamic, 0, 0, 2)
                        // dynamicLayout.addLineSeperator(lytDynamic);
                        dynamicLayout?.addSpinners(
                            ui.lytDynamic, internalId, 0,
                            "\u2022 $paramName ", values
                        ) { value ->
                            paramsArrayListToSend.add(
                                TotalAmountPojoModel.Params(
                                    internalId,
                                    value
                                )
                            )
                        }
                        val spinner: Spinner = ui.lytDynamic.findViewWithTag(internalId)
                        // set value to spinner
                        setSpinnerValue(internalId, spinner, values)
                        spinner.visibility =
                            if (cardParameters.contains(internalId)) View.GONE else View.VISIBLE
                    }

                    Constants.Radio -> {
                        dynamicLayout?.addTextViews(ui.lytDynamic,  /*"\u2022 " +*/ paramName)
                        // adding view
                        dynamicLayout?.addViews(ui.lytDynamic, 0, 0, 2)
                        // dynamicLayout.addLineSeperator(lytDynamic);
                        dynamicLayout?.addRadioButtons(ui.lytDynamic, internalId, values)
                    }

                    else -> {}
                }
            }

            //}

            // adding view
            dynamicLayout?.addViews(ui.lytDynamic, 0, 0, 2)
        }


        if (serviceViewModel.acceptAmountinput == 1) {
            // adding title
            dynamicLayout!!.addTextViews(
                ui.lytDynamic,  /*"\u2022 " +*/
                getString(R.string.params_amount) + " "
            )
            // adding view
            dynamicLayout!!.addViews(ui.lytDynamic, 0, 0, 2)
            // adding edit text
            dynamicLayout!!.addEditTexts(
                ui.lytDynamic,
                true,
                "amount",
                resources.getString(R.string.params_amount),
                InputType.TYPE_CLASS_NUMBER,
                1
            ) { }

            val edtAmount: EditText = ui.lytDynamic.findViewWithTag("amount")

            edtAmount.inputType =

                InputType.TYPE_CLASS_NUMBER or
                        InputType.TYPE_NUMBER_FLAG_DECIMAL or
                        InputType.TYPE_NUMBER_FLAG_SIGNED
            edtAmount.setText("")
        }
    }

    var someActivityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == -1) {
            if (result.data != null) {
                result.data?.let { contactPicked(it) }
            }
        }
    }

    private fun startActivity(internalId: String) {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
        intent.putExtra(Constants.INTERNAL_ID, internalId)
        this.internalId = internalId
        someActivityResultLauncher.launch(intent)

//        startActivityForResult(
//            intent,
//            RESULT_PICK_CONTACT
//        )
    }

    private fun contactPicked(data: Intent) {
        try {
            val cursor = Objects.requireNonNull(data.data?.let {
                this.contentResolver.query(
                    it,
                    null, null, null, null
                )
            })
            cursor?.moveToFirst()
            val phoneIndex = cursor?.getColumnIndex("data1")
            val phoneNo =
                phoneIndex?.let {
                    cursor.getString(it)?.replace(
                        " ",
                        ""
                    )?.replace("+", "")?.trim { it <= ' ' }
                }

            // val result = data.getStringExtra(Constants.INTERNAL_ID)
            val editTextTag = internalId
            val edtNumber: EditText = ui.lytDynamic.findViewWithTag(editTextTag)
            edtNumber.setText(
                phoneNo?.replace("-", "")?.replace(" ", "")?.replace("(", "")?.replace(")", "")
            )
            cursor?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setSpinnerValue(
        internalId: String,
        spinner: Spinner,
        values: ArrayList<SpinnerModel>
    ) {
        for (i in DATA_ENTITY?.parameters!!.indices) {
            val dbInternalId: String = DATA_ENTITY!!.parameters!![i].internalId
            val value: String = DATA_ENTITY!!.parameters[i].value

            if (dbInternalId == internalId) {
                for (ii in values.indices) {
                    if (value == values[ii].id || value == values[ii].getaName()) {
                        spinner.setSelection(ii)
                        spinner.isEnabled = false
                    }
                }
            }
        }
    }

    private fun setValue(internalId: String, editText: EditText) {
        for (i in DATA_ENTITY?.parameters!!.indices) {
            val dbInternalId: String = DATA_ENTITY!!.parameters[i].internalId ?: ""

            val value: String = DATA_ENTITY!!.parameters[i].value ?: ""

            if (dbInternalId == internalId) {
                editText.setText(value)
                editText.isEnabled = false
            }

        }
    }

    private fun getTotalAmountForPay(transactionType: String, amountForPay: String) {

        lifecycleScope.launch {

            paytabsViewModel.getTotalPay(sharedHelper?.getUserToken().toString(),
                paymentMethodType = GatewayMethod.paytabs.toString(),
                transactionType = transactionType,
                amount = amountForPay, object : OnResponseListener {
                    override fun onSuccess(code: Int, msg: String?, obj: Any?) {

                        pDialog.cancel()

                        val data = obj as Data

                        val amount = java.lang.String.format(
                            " • %s %s %s",
                            getString(R.string.dialog_amount),
                            Utils.format(amountForPay.toDouble()),
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
                                NavigateToActivity.navigateToAuthActivity(this@InquireActivity)
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
        // Amount will in user wallet
        // totalAmount will take from visa or wallet

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
                                        this@InquireActivity,
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
            .setCartId(Constants.HASH_ID)
            .setBillingData(billingData)
            .setShippingData(shippingData)
            .setScreenTitle(transactionTitle)

        return configData.build()
    }

    private fun extractNumber(input: String): String? {
        val regex = Regex("(\\d+\\.?\\d*)")
        val matchResult = regex.find(input)
        return matchResult?.value
    }

    private fun submitBtn() {

        finalTotalAmount = extractNumber(ui.tvTotalAmou.text.toString()).toString()
        finalAmount = extractNumber(ui.tvAmount.text.toString()).toString()

        when (finalPaymentWay) {
            PayWays.CASH.toString() -> {
                pDialog.show()
                getTotalWithCash()
            }

            PayWays.Esh7enly.toString() -> {

                dialog.showSuccessDialogWithAction(
                    resources.getString(R.string.confirmation_title),
                    resources.getString(R.string.msg_confirm_pay)
                            + "\n" + finalTotalAmount +
                            " EGP  ?",
                    resources.getString(R.string.app__ok),
                    resources.getString(R.string.app__cancel)
                ) {
                    if (getParamsData()) {
                        dialog.cancel()

//                            val paymentPojoModel = PaymentPojoModel(
//                                Constants.IMEI,
//                                "",
//                                SERVICE_ID,
//                                DATA_ENTITY?.amount.toString(),
//                                DATA_ENTITY?.id.toString(),
//                                "",
//                                paramsArrayListToSend
//                            )


                        val paymentPojoModel = PaymentPojoModel(
                            Constants.IMEI,
                            "",
                            SERVICE_ID,
                            finalAmount,
                            DATA_ENTITY!!.id.toString(),
                            "",
                            PAYMENTPOJOMODEL!!.params
                        )

                        if (ACCEPT_CHECK_INTEGRATION_PROVIDER_STATUS == 1) {
                            // check integration
                            checkIntegration(paymentPojoModel)
                        } else {
                            // pay
                            pay(paymentPojoModel)
                        }
                    }
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

                    getTotalAmountForPay(
                        transactionType = GatewayTransactionType.wallet.toString(),
                        amountForPay = finalTotalAmount
                    )

                }.show()
            }

            PayWays.BANk.toString() -> {

                dialog.showWarningDialogWithAction(
                    resources.getString(R.string.payment_warning),
                    resources.getString(R.string.app__ok)
                )
                {
                    dialog.cancel()

                    pDialog.show()

                    getTotalAmountForPay(
                        transactionType = GatewayTransactionType.visa.toString(),
                        amountForPay = finalTotalAmount
                    )

                }.show()

            }

            else -> {
                Toast.makeText(this, "Please select way", Toast.LENGTH_SHORT).show()
            }
        }

//            dialog.showSuccessDialogWithAction(
//                resources.getString(R.string.confirmation_title),
//                resources.getString(R.string.msg_confirm_pay)
//                        +"\n"+ DATA_ENTITY?.totalAmount +
//                        " EGP  ?",
//                resources.getString(R.string.app__ok), resources.getString(R.string.app__cancel)
//            ) {
//                if (getParamsData()) {
//                    dialog.cancel()
//
//                    val paymentPojoModel = PaymentPojoModel(
//                        Constants.IMEI, "", SERVICE_ID,
//                        DATA_ENTITY?.amount.toString(), DATA_ENTITY?.id.toString(),
//                        "", paramsArrayListToSend
//                    )
//
//                    if (ACCEPT_CHECK_INTEGRATION_PROVIDER_STATUS == 1) {
//                        // check integration
//                        checkIntegration(paymentPojoModel)
//                    } else {
//                        // pay
//                        pay(paymentPojoModel)
//                    }
//                }
//            }.show()

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
                finalTotalAmount, mutableListOf(params)
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
                                finalTotalAmount,
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

        lifecycleScope.launch {
            serviceViewModel.pay(sharedHelper?.getUserToken().toString(), paymentPojoModel,
                object : OnResponseListener {
                    override fun onSuccess(code: Int, msg: String?, obj: Any?) {
                        pDialog.cancel()

                        val paymentPojoModel = PaymentPojoModel(
                            Constants.IMEI,
                            "",
                            SERVICE_ID,
                            finalAmount,
                            DATA_ENTITY!!.id.toString(),
                            "",
                            PAYMENTPOJOMODEL!!.params
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

    override fun pay(paymentPojoModel: PaymentPojoModel) {
        pDialog.show()

        lifecycleScope.launch(Dispatchers.IO)
        {
            // Add to database for cancelling later if need

            serviceViewModel.insertToFawryDao(
                FawryEntity(
                    paymentPojoModel.paymentTransactionId.toInt(),
                    paymentPojoModel.imei, Calendar.getInstance().timeInMillis
                )
            )

            serviceViewModel.pay(sharedHelper?.getUserToken().toString(),
                paymentPojoModel, object : OnResponseListener {
                    override fun onSuccess(code: Int, msg: String?, obj: Any?) {
                        // pDialog.cancel()

                        val result = obj as PaymentEntity.DataEntity

                        serviceViewModel.deleteFawryOperations(paymentPojoModel.paymentTransactionId.toInt())

                        if (ServicesCard.ELECTRICITY_BTC.contains(SERVICE_ID) ||
                            ServicesCard.WATER_BTC.contains(SERVICE_ID) ||
                            ServicesCard.GAS_BTC.contains(SERVICE_ID)
                        ) {
                            // Add to database for cancelling if write on card not success
                            serviceViewModel.insertToFawryDao(
                                FawryEntity(
                                    result.id, result.imei,
                                    Calendar.getInstance().timeInMillis
                                )
                            )

                            writeOnCard(result)

                        } else {
                            lifecycleScope.launch { scheduleInquire(result) }
                        }

                    }

                    override fun onFailed(code: Int, msg: String?) {
                        serviceViewModel.deleteFawryOperations(paymentPojoModel.paymentTransactionId.toInt())

                        pDialog.cancel()

                        dialog.showErrorDialogWithAction(
                            msg, resources.getString(R.string.app__ok)
                        ) {
                            dialog.cancel()

                            if (code == Constants.CODE_UNAUTH_NEW ||
                                code.toString() == Constants.CODE_HTTP_UNAUTHORIZED
                            ) {
                                NavigateToActivity.navigateToAuthActivity(this@InquireActivity)
                            }
                        }.show()
                    }
                })
        }
    }

    private fun scheduleInquire(result: PaymentEntity.DataEntity) {
        serviceViewModel.scheduleInquire(sharedHelper?.getUserToken().toString(),
            result.service.id.toString(), result.clientNumber,
            object : OnResponseListener {
                override fun onSuccess(code: Int, msg: String?, obj: Any?) {
                    pDialog.cancel()

                    val builder = AlertDialog.Builder(this@InquireActivity)
                        .setMessage(resources.getString(R.string.add_to_reminder))
                        .setTitle(resources.getString(R.string.alert))
                        .setCancelable(false)
                        .setPositiveButton(resources.getString(R.string.add))
                        { alertDialog, _ ->
                            alertDialog.cancel()

                            val calendar = Calendar.getInstance()
                            val day = calendar.get(Calendar.DATE).toString()

                            pDialog.show()

                            lifecycleScope.launch { scheduleInvoice(result, day) }
                        }
                        .setNegativeButton(resources.getString(R.string.no_add))
                        { alertDialog, _ ->
                            alertDialog.cancel()
                            ReceiptActivity.getIntent(
                                this@InquireActivity,
                                result, serviceViewModel.serviceType
                            )
                        }

                    val alertDialog = builder.create()
                    alertDialog.show()
                }

                override fun onFailed(code: Int, msg: String?) {
                    pDialog.cancel()

                    ReceiptActivity.getIntent(
                        this@InquireActivity,
                        result, serviceViewModel.serviceType
                    )
                    // finish()

                }

            })
    }

    private fun scheduleInvoice(result: PaymentEntity.DataEntity, day: String) {
        serviceViewModel.scheduleInvoice(sharedHelper?.getUserToken().toString(),
            result.service.id.toString(),
            day, result.clientNumber,
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

                        ReceiptActivity.getIntent(
                            this@InquireActivity,
                            result, serviceViewModel.serviceType
                        )

                        //  finish()
                    }
                    dialog.show()
                }

                override fun onFailed(code: Int, msg: String?) {
                    pDialog.cancel()

                    dialog.showErrorDialogWithAction(
                        msg, resources.getString(R.string.app__ok)
                    ) {
                        dialog.cancel()

                        ReceiptActivity.getIntent(
                            this@InquireActivity,
                            result, serviceViewModel.serviceType
                        )

                        // finish()

                    }.show()
                }

            })
    }

    private fun writeOnCard(result: PaymentEntity.DataEntity) {
        val data = result.description.split("\n")

        val cardData = data[0].split(":")[1].trim()

        val cardMetaData = data[1].split(":")[1].trim()

        Log.d(TAG, "diaa write on card description: ${result.description}")
        Log.d(TAG, "diaa write on card data: $data")
        Log.d(TAG, "diaa write on card cardData: $cardData")
        Log.d(TAG, "diaa write on card cardMetaData: $cardMetaData")

        val nfcCard = NFCFawry()

        nfcCard.WriteNewNFCCard(
            this,
            Constants.nfcCard,
            Constants.BILLING_ACCOUNT_CARD,
            cardData,
            cardMetaData,
            object : NFCWriteCallback {
                override fun onStartReadNFCCard() {
                    message("Start read")
                }

                override fun onCardNotSupported() {
//                    pDialog.cancel()
//                    cancelTransaction(result)
//                    message("Not supported write")
                }

                override fun onDeviceNotSupportedNFC() {
//                    pDialog.cancel()
//               //     cancelTransaction(result)
//                    message("Nfc not supported")
                }

                override fun onCardWriteError(vararg exception: Exception) {
//                    pDialog.cancel()
//                    cancelTransaction(result)
                    // message("Write error" + exception[0]?.message)
                }

                override fun onMismatchNFCCard() {
//                     pDialog.cancel()
//                     cancelTransaction(result)
//                     message("Missmatch")

                }

                override fun onSuccessWriteNFCCard(response: FawryWrapperResponse) {
                    //  pDialog.cancel()

                    if (response.status == FawryWapperStatus.Status.SUCCESSFUL) {
                        Log.d(TAG, "diaa on success write: ${response.data}")
                        Log.d(TAG, "diaa on success message: ${response.message}")
                        Log.d(TAG, "diaa on success status: ${response.status}")
                        message("Success write")
                        serviceViewModel.deleteFawryOperations(result.id)

                        lifecycleScope.launch { scheduleInquire(result) }
                    } else {
                        pDialog.cancel()
                        cancelTransaction(result)
                        message(response.status.toString())
                    }
                }
            })
    }

    private fun cancelTransaction(result: PaymentEntity.DataEntity) {

        serviceViewModel.cancelTransaction(sharedHelper?.getUserToken().toString(),
            result.id.toString(),
            result.imei, object : OnResponseListener {
                override fun onSuccess(code: Int, msg: String?, obj: Any?) {
                    serviceViewModel.deleteFawryOperations(result.id)
                    showMessage(" تم استرجاع المبلغ اليكم $msg ")
                }

                override fun onFailed(code: Int, msg: String?) {
                    showMessage(" فشل استرجاع المبلغ$msg")

                    serviceViewModel.insertToFawryDao(
                        FawryEntity(
                            result.id, result.imei,
                            Calendar.getInstance().timeInMillis
                        )
                    )
                }
            })
    }

    private fun message(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }

    private fun checkIntegration(paymentPojoModel: PaymentPojoModel) {
        pDialog.show()

        lifecycleScope.launch(Dispatchers.IO) {
            serviceViewModel.checkIntegration(sharedHelper?.getUserToken().toString(),
                DATA_ENTITY?.id.toString(), "", object : OnResponseListener {
                    override fun onSuccess(code: Int, msg: String?, obj: Any?) {
                        pDialog.cancel()
                        pay(paymentPojoModel)
                    }

                    override fun onFailed(code: Int, msg: String?) {
                        pDialog.cancel()
                        dialog.showErrorDialogWithAction(
                            msg, resources.getString(R.string.app__ok)
                        ) {
                            dialog.cancel()

                            if (code == Constants.CODE_UNAUTH_NEW
                                || code.toString() == Constants.CODE_HTTP_UNAUTHORIZED
                            ) {
                                NavigateToActivity.navigateToAuthActivity(this@InquireActivity)

                            }
                        }.show()
                    }
                })
        }
    }

    private fun getParamsData(): Boolean {
        // get Params
        for (i in this.parametersList.indices) {
            // Params Details
            //   val serviceId: Int = this.parametersList[i].service_id
            val internalId: String = this.parametersList[i].internal_id
            val type: Int = this.parametersList[i].type
            //   val paramNameAr: String = this.parametersList[i].name_ar
            //  val paramNameEn: String = this.parametersList[i].name_en
            val minLength: Int = this.parametersList[i].min_length
            val maxLength: Int = this.parametersList[i].max_length
            val required: Int = this.parametersList[i].required
            val display: String = this.parametersList[i].display.toString()
            var shouldBreak = false
            val values = ArrayList<SpinnerModel>()

            for (ii in 0 until parametersList[i].type_values.size) {
                values.add(
                    SpinnerModel(
                        java.lang.String.valueOf(
                            parametersList[i].type_values[ii].value
                        ),
                        parametersList[i].type_values[ii].name_ar,
                        parametersList[i].type_values[ii].name_en
                    )
                )
            }
            if (display == Constants.DISPLAY_FOR_ALL || display == Constants.DISPLAY_FOR_PAYMENT) {
                when (type) {
                    Constants.Number -> {
                        val etNumber: EditText =
                            ui.lytDynamic.findViewWithTag(internalId)
                        val valueNumber = Utils.replaceArabicNumbers(etNumber.text.toString())
                        if (required == 1 && valueNumber.isEmpty()) {
                            etNumber.error = getString(R.string.required)
                            shouldBreak = true
                        } else if (valueNumber.length < minLength || valueNumber.length > maxLength) {
                            if (minLength == maxLength) {
                                etNumber.error =
                                    getString(R.string.required_value_number) + " " + minLength + getString(
                                        R.string.number
                                    )
                            } else {
                                etNumber.error =
                                    getString(R.string.required_value_range) + " " + minLength + " " + getString(
                                        R.string.number
                                    ) + getString(R.string.and) + " " + getString(R.string.number) + maxLength
                            }
                            shouldBreak = true
                        }
                        paramsArrayListToSend.add(
                            TotalAmountPojoModel.Params(
                                internalId,
                                valueNumber
                            )
                        )
                    }

                    Constants.Char -> {
                        val etChar: EditText = ui.lytDynamic.findViewWithTag(internalId)
                        val value = etChar.text.toString()
                        if (required == 1 && value.isEmpty()) {
                            etChar.error = getString(R.string.required)
                            shouldBreak = true
                        }
                        paramsArrayListToSend.add(TotalAmountPojoModel.Params(internalId, value))
                    }

                    Constants.Date -> {
                        val etDate: EditText = ui.lytDynamic.findViewWithTag(internalId)
                        val etDateValue = Utils.replaceArabicNumbers(etDate.text.toString())
                        if (required == 1 && etDateValue.isEmpty()) {
                            etDate.error = getString(R.string.required)
                            shouldBreak = true
                        }
                        paramsArrayListToSend.add(
                            TotalAmountPojoModel.Params(
                                internalId,
                                etDateValue
                            )
                        )
                    }

                    Constants.textarea -> {
                        val etTextArea: EditText =
                            ui.lytDynamic.findViewWithTag(internalId)
                        val valueTxtArea = etTextArea.text.toString()
                        if (required == 1 && valueTxtArea.isEmpty()) {
                            etTextArea.error = getString(R.string.required)
                            shouldBreak = true
                        }
                        paramsArrayListToSend.add(
                            TotalAmountPojoModel.Params(
                                internalId,
                                valueTxtArea
                            )
                        )
                    }

                    Constants.Select -> {
                        val spinner: Spinner = ui.lytDynamic.findViewWithTag(internalId)
                        val spinnerSelectedName = spinner.selectedItem.toString()
                        if (required == 1 && spinnerSelectedName.lowercase(Locale.getDefault())
                                .contains("select")
                        ) {
                            showMessage(internalId + getString(R.string.required))
                            shouldBreak = true
                        }
                        var iii = 0
                        while (iii < values.size) {
                            if (spinnerSelectedName == values[iii].getaName() || spinnerSelectedName == values[iii].geteName()) {
                                paramsArrayListToSend.add(
                                    TotalAmountPojoModel.Params(
                                        internalId,
                                        values[iii].id
                                    )
                                )
                            }
                            iii++
                        }
                    }

                    Constants.Radio -> {
                        val radioGroup: RadioGroup =
                            ui.lytDynamic.findViewWithTag(internalId)
                        // get selected radio button from radioGroup
                        val selectedId = radioGroup.checkedRadioButtonId
                        // find the radiobutton by returned id
                        val radioButton = radioGroup.findViewById<View>(selectedId) as RadioButton
                        if (required == 1) {
                            if (!radioButton.isChecked) {
                                showMessage(internalId + getString(R.string.required))
                                shouldBreak = true
                            } else {
                                val radiovalue = radioButton.text.toString()
                                var iii = 0
                                while (iii < values.size) {
                                    if (radiovalue == values[iii].getaName() || radiovalue == values[iii].geteName()) {
                                        paramsArrayListToSend.add(
                                            TotalAmountPojoModel.Params(
                                                internalId,
                                                values[iii].id
                                            )
                                        )
                                    }
                                    iii++
                                }
                            }
                        }
                    }

                    else -> {}
                }
            }
            if (shouldBreak) return false
        }
        return true
    }

    private fun showMessage(message: String?) {

        lifecycleScope.launch(Dispatchers.Main)
        {
            if (message != null) {
                Toast.makeText(this@InquireActivity, message, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    this@InquireActivity,
                    getString(R.string.some_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun showEditAmountDialog() {

        dialog.showDialogEditAmount(
            resources.getString(R.string.confirmation_title),
            resources.getString(R.string.dialog_editable_amount_msg) + " " +
                    DATA_ENTITY?.minAmount + " " + resources.getString(R.string.and) + DATA_ENTITY?.maxAmount,
            resources.getString(R.string.app__ok), resources.getString(R.string.app__cancel),
            DATA_ENTITY?.amount.toString()
        ) { amount ->

            dialog.cancel()

            pDialog.show()

            editedAmount = amount!!

            val totalAmountPojoModel = TotalAmountPojoModel(
                Constants.IMEI, SERVICE_ID, amount,
                DATA_ENTITY!!.id, PAYMENTPOJOMODEL!!.params
            )

            serviceViewModel.getTotalAmount(sharedHelper?.getUserToken().toString(),
                totalAmountPojoModel,
                object : OnResponseListener {
                    override fun onSuccess(code: Int, msg: String?, obj: Any?) {

                        pDialog.cancel()

                        val data = obj as TotalAmountEntity.DataEntity

                        val paidAmount = ""

                        ui.tvAmount.text =
                            Utils.format(data.amount) + resources.getString(R.string.egp)
                        ui.tvPaidAmou.text = paidAmount

                        ui.tvTotalAmou.text =
                            Utils.format(data.totalAmount) + resources.getString(R.string.egp)

                        ui.tvFee.text =
                            Utils.format(data.serviceCharge) + resources.getString(R.string.egp)

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
                                    NavigateToActivity.navigateToAuthActivity(this@InquireActivity)
                                }
                            }
                        }.show()
                    }

                })
        }
        dialog.show()
    }

    override fun onError(error: PaymentSdkError) {

        val chargeBalanceRequest = ChargeBalanceRequestPaytabs(
            status = PaymentStatus.FAILED.toString(),
            id = Constants.START_SESSION_ID,
            amount = finalTotalAmount,
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

            val paymentPojoModel = PaymentPojoModel(
                Constants.IMEI,
                "",
                SERVICE_ID,
                finalAmount,
                DATA_ENTITY!!.id.toString(),
                "",
                PAYMENTPOJOMODEL!!.params
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

    override fun onPaymentFinish(paymentSdkTransactionDetails: PaymentSdkTransactionDetails) {

        var chargeBalanceRequest = ChargeBalanceRequestPaytabs(
            id = Constants.START_SESSION_ID,
            // amount = DATA_ENTITY?.totalAmount.toString(),
            amount = finalTotalAmount,
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

        val paymentPojoModel = PaymentPojoModel(
            Constants.IMEI,
            "",
            SERVICE_ID,
            finalAmount,
            DATA_ENTITY!!.id.toString(),
            "",
            PAYMENTPOJOMODEL!!.params
        )

        requestToChargeBalance(
            paymentPojoModel = paymentPojoModel,
            chargeBalanceRequest = chargeBalanceRequest
        )
    }

}