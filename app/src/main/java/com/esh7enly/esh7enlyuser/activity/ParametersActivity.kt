package com.esh7enly.esh7enlyuser.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.NfcManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.InputType
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.esh7enly.domain.entity.*
import com.esh7enly.domain.entity.PaymentEntity.DataEntity
import com.esh7enly.domain.entity.TotalAmountPojoModel.Params
import com.esh7enly.domain.entity.parametersNew.ParametersData
import com.esh7enly.domain.entity.userservices.Image
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.click.OnResponseListener
import com.esh7enly.esh7enlyuser.databinding.ActivityParametersBinding
import com.esh7enly.esh7enlyuser.imageslider.AdapterImageSlider
import com.esh7enly.esh7enlyuser.imageslider.ItemImageSlider
import com.esh7enly.esh7enlyuser.util.*
import com.fawry.nfc.NFC.Main.NFCFawry
import com.fawry.nfc.NFC.Shared.NFCConstants
import com.fawry.nfc.NFC.interfaces.NFCReadCallback
import com.fawry.nfc.NFC.interfaces.NFCReadCallbackResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import java.util.*
import javax.inject.Inject

private const val TAG = "ParametersActivity"

@AndroidEntryPoint
open class ParametersActivity : BaseActivity() {
    private val ui by lazy {
        ActivityParametersBinding.inflate(layoutInflater)
    }

    private var nfcAdapter: NfcAdapter? = null
    private var manager: NfcManager? = null

    lateinit var itemImageSlider: ItemImageSlider

    private var internalId: String? = null

    private val PICK_CONTACT_REQUESTCODE = 100


    private var amount = ""

    private val lang by lazy {
        Constants.LANG
    }

    var dynamicLayout: DynamicLayout? = null
        @Inject set


    private val image = arrayListOf<Image>()
    private var parametersList: List<ParametersData> = emptyList()

    private var paramsArrayListToSend = arrayListOf<Params>()

    private var serviceName: String? = null

    private val dialog by lazy {
        AppDialogMsg(this, false)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)

        Language.setLanguageNew(this, Constants.LANG)


        manager = getSystemService(Context.NFC_SERVICE) as NfcManager

        nfcAdapter = manager!!.defaultAdapter

//        pDialog.setMessage(Utils.getSpannableString(this,resources.getString(R.string.message__please_wait)))
//        pDialog.setCancelable(false)

        ui.btnSubmit.setOnClickListener { pushSubmitBtn() }

        getIntentData()

        serviceName =
            if (Constants.LANG == Constants.AR) Constants.SERVICE_NAME_AR else Constants.SERVICE_NAME_EN

        initToolBar()

        showData()

    }

    private fun showData() {
        ui.tvService.text = serviceName
        ui.tvProvider.text = serviceViewModel.providerName

        if (serviceViewModel.image == null) {
            ui.img.setImageResource(R.drawable.logo)
        } else {
            Utils.displayImageOriginalFromCache(
                this, ui.img,
                serviceViewModel.image, NetworkUtils.isConnectedWifi(this)
            )
        }


        if (serviceViewModel.serviceType == Constants.INQUIRY_PAYMENT) {
            ui.btnSubmit.text = resources.getString(R.string.inquiry)
        } else if (serviceViewModel.serviceType == Constants.PAYMENT) {
            ui.btnSubmit.text = resources.getString(R.string.pay)
        }

        getParametersFromRemote()

        getImagesFromDB()
    }

    private fun getImagesFromDB() {
        serviceViewModel.getImagesFromDB(serviceViewModel.servicesId.toString())
            .observe(this@ParametersActivity)
            { images ->
                if (images != null) {
                    //    val newImages= images.first()

                    image.addAll(images)

                    if (images.isNotEmpty()) {
                        ui.cardSlider.visibility = View.VISIBLE
                        val adapterImageSlider = AdapterImageSlider(
                            this@ParametersActivity,
                            image
                        )

                        itemImageSlider = ItemImageSlider(
                            this@ParametersActivity, ui.pager,
                            ui.layoutDots, adapterImageSlider, image
                        )

                        itemImageSlider.initImageSlider()
                    }
                }

            }
    }

    private fun getParametersFromDB()
    {
        //        serviceViewModel.getParametersFromDB(serviceViewModel.servicesId.toString())
//            .observe(this)
//            {
//                    parameters->
//                if(ServicesCard.ELECTRICITY_BTC.contains(serviceViewModel.servicesId) ||
//                    ServicesCard.WATER_BTC.contains(serviceViewModel.servicesId) ||
//                    ServicesCard.GAS_BTC.contains(serviceViewModel.servicesId))
//                {
//                    // Electricity or water card
//                    ui.lytDynamic.visibility = View.GONE
//                    ui.cardReading.visibility = View.VISIBLE
//                }
//                else
//                {
//                    ui.lytDynamic.visibility = View.VISIBLE
//                    ui.cardReading.visibility = View.GONE
//
//                    replaceData(parameters)
//                }
//            }
    }

    private fun getParametersFromRemote() {
        pDialog.show()

        serviceViewModel.getParameters(sharedHelper?.getUserToken().toString(),
            serviceViewModel.servicesId.toString(), object : OnResponseListener {
                override fun onSuccess(code: Int, msg: String?, obj: Any?) {
                    pDialog.cancel()
                    val parameters = obj as List<ParametersData>

                    if (ServicesCard.ELECTRICITY_BTC.contains(serviceViewModel.servicesId) ||
                        ServicesCard.WATER_BTC.contains(serviceViewModel.servicesId) ||
                        ServicesCard.GAS_BTC.contains(serviceViewModel.servicesId)
                    ) {
                        // Electricity or water card
                        ui.lytDynamic.visibility = View.GONE
                        ui.cardReading.visibility = View.VISIBLE
                    } else {
                        ui.lytDynamic.visibility = View.VISIBLE
                        ui.cardReading.visibility = View.GONE

                        replaceData(parameters)
                    }
                }

                override fun onFailed(code: Int, msg: String?) {
                    pDialog.cancel()

                    Log.d("TAG", "diaa onFailed: $msg")

                    dialog.showErrorDialogWithAction(
                        msg, resources.getString(R.string.app__ok)
                    ) {
                        dialog.cancel()

                        if (code.toString() == Constants.CODE_UNAUTH ||
                            code.toString() == Constants.CODE_HTTP_UNAUTHORIZED
                        ) {
                            NavigateToActivity.navigateToMainActivity(this@ParametersActivity)
                        }
                    }.show()
                }
            })

    }

    private fun initToolBar() {
        ui.parametersToolbar.title = serviceName

        ui.parametersToolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun pushSubmitBtn() {
        if (connectivity?.isConnected == true) {
            if (ServicesCard.ELECTRICITY_BTC.contains(serviceViewModel.servicesId)) {
                // Electricity card
                initializeNFCCard(NFCConstants.CardType.ELECT)
                Constants.nfcCard = NFCConstants.CardType.ELECT
            } else if (ServicesCard.WATER_BTC.contains(serviceViewModel.servicesId)) {
                // Water card
                initializeNFCCard(NFCConstants.CardType.WSC)
                Constants.nfcCard = NFCConstants.CardType.WSC

            } else if (ServicesCard.GAS_BTC.contains(serviceViewModel.servicesId)) {
                // Gas card
                initializeNFCCard(NFCConstants.CardType.GAS)
                Constants.nfcCard = NFCConstants.CardType.GAS
            } else {
                // Getting amount value if this service need it
                if (serviceViewModel.acceptAmountinput == 1) {

                    // find amount EdtTxt by id
                    val etAmount: EditText = ui.lytDynamic.findViewWithTag("amount")
                    // get Amount By User
                    amount = Utils.replaceArabicNumbers(etAmount.text.toString())
                } else {
                    if (serviceViewModel.priceType == 2) {
                        // get Specific Amount from DB
                        amount = serviceViewModel.priceValue.toString()
                    }

                }

                //Clear list from old data before fill it
                paramsArrayListToSend.clear()

                // collect Params data before Make inquiry
                // use this boolean check to make dynamic inputs required Validations
                if (getParamsData()) {
                    if (serviceViewModel.serviceType == Constants.INQUIRY_PAYMENT) {
                        sendParametersToAPI()
                    } else if (serviceViewModel.serviceType == Constants.PAYMENT) {

                        pDialog.show()
                        //  pDialog.show()

                        val totalAmountPojoModel = TotalAmountPojoModel(
                            Constants.IMEI,
                            serviceViewModel.servicesId, amount, paramsArrayListToSend
                        )
                        lifecycleScope.launch(Dispatchers.IO)
                        {
                            getTotalAmount(totalAmountPojoModel)
                        }
                    }
                }

            }
        } else {
            // Show dialog
            dialog.showWarningDialog(
                resources.getString(R.string.no_internet_error),
                resources.getString(R.string.app__ok)
            )
            dialog.show()
        }
    }

    private fun initializeNFCCard(cardType: NFCConstants.CardType) {
        val nfcCard = NFCFawry()

        nfcCard.ReadNewNFCCard(this, cardType, object : NFCReadCallback {
            override fun onStartReadNFCCard() {
                message("Start")
            }

            override fun onCardNotSupported() {
                message("Not supported")
            }

            override fun onDeviceNotSupportedNFC() {
                message("Nfc not supported")
            }

            override fun onCardReadError(vararg exception: java.lang.Exception) {
                message("Read error")
            }

            override fun onChargeExist() {
                message("Charge exist")
            }

            override fun onSuccessReadNFCCard(data: NFCReadCallbackResponse) {

                message("Success read")

                paramsArrayListToSend.add(Params("ClientIdentifier", data.clientIdentifier))
                // paramsArrayListToSend.add(Params("ClientIdentifier", client))
                paramsArrayListToSend.add(Params("billing_account", data.bilingAccount))
                Constants.BILLING_ACCOUNT_CARD = data.bilingAccount
                paramsArrayListToSend.add(Params("CardMetadata", data.cardMetadata))
                paramsArrayListToSend.add(Params("Key1", data.companyVendor))
                paramsArrayListToSend.add(Params("Key2", data.sectorIdentifier))
                paramsArrayListToSend.add(Params("Key3", data.meterIdentifier))
                paramsArrayListToSend.add(Params("ESCFBT01", data.escfbT01))
                paramsArrayListToSend.add(Params("ESCFBT02", data.escfbT02))
                paramsArrayListToSend.add(Params("ESCFBT03", data.escfbT03))

                when (cardType) {
                    NFCConstants.CardType.ELECT -> paramsArrayListToSend.add(
                        Params(
                            "ElectrictyCompany",
                            data.companyIdentifier
                        )
                    )

                    NFCConstants.CardType.WSC -> paramsArrayListToSend.add(
                        Params(
                            "WaterCompany",
                            data.companyIdentifier
                        )
                    )

                    NFCConstants.CardType.GAS -> paramsArrayListToSend.add(
                        Params(
                            "GasCompany",
                            data.companyIdentifier
                        )
                    )
                }

                sendParametersToAPI()
            }

            override fun onUnifiedCardDetection(b: Boolean) {
                message("Detection")
            }
        })
    }

    private fun message(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun getTotalAmount(totalAmountPojoModel: TotalAmountPojoModel) {
        serviceViewModel.getTotalAmount(sharedHelper?.getUserToken().toString(),
            totalAmountPojoModel,
            object : OnResponseListener {
                override fun onSuccess(code: Int, msg: String?, obj: Any?) {
                    // pDialog.cancel()
                    pDialog.cancel()

                    val data = obj as TotalAmountEntity.DataEntity

                    val amount = java.lang.String.format(
                        " • %s %s %s",
                        getString(R.string.dialog_amount),
                        Utils.format(data.amount),
                        getString(R.string.egp)
                    )

                    val totalAmount = java.lang.String.format(
                        " • %s %s %s",
                        getString(R.string.dialog_total_amount),
                        Utils.format(data.totalAmount),
                        getString(R.string.egp)
                    )

                    val paidAmount = ""

                    dialog.showSuccessDialogWithAction(
                        resources.getString(R.string.confirmation_title),
                        String.format(
                            "%s \n %s \n %s",
                            amount,
                            totalAmount,
                            paidAmount
                        ),
                        resources.getString(R.string.app__ok),
                        resources.getString(R.string.app__cancel)
                    ) {
                        dialog.cancel()

                        val paymentPojoModel =
                            PaymentPojoModel(
                                Constants.IMEI,
                                "",
                                totalAmountPojoModel.serviceId,
                                totalAmountPojoModel.amount,
                                totalAmountPojoModel.attributes
                            )

                        pay(paymentPojoModel)

                    }.show()
                }

                override fun onFailed(code: Int, msg: String?) {
                    pDialog.cancel()
//                    pDialog.cancel()
                    dialog.showErrorDialogWithAction(
                        msg,
                        resources.getString(R.string.app__ok)
                    ) {
                        dialog.cancel()
                        if (code.toString() == Constants.CODE_UNAUTH || code.toString() == Constants.CODE_HTTP_UNAUTHORIZED) {
                            NavigateToActivity.navigateToMainActivity(this@ParametersActivity)
                        }

                    }.show()
                }
            })
    }

    private fun pay(paymentPojoModel: PaymentPojoModel) {
//        pDialog.show()
        pDialog.show()


        serviceViewModel.pay(sharedHelper?.getUserToken().toString(), paymentPojoModel,
            object : OnResponseListener {
                override fun onSuccess(code: Int, msg: String?, obj: Any?) {
                    //  pDialog.cancel()
                    val data = obj as DataEntity
                    // Move to print activity
                    clearParamsData()

                    lifecycleScope.launch { scheduleInquire(data) }

//                    ReceiptActivity.getIntent(
//                        this@ParametersActivity,
//                        data, serviceViewModel.serviceType
//                    )
                }

                override fun onFailed(code: Int, msg: String?) {
//                    pDialog.cancel()
                    pDialog.cancel()


                    clearParamsData()

                    dialog.showErrorDialogWithAction(
                        msg,
                        resources.getString(R.string.app__ok)
                    ) {
                        dialog.cancel()
                        if (code.toString() == Constants.CODE_UNAUTH || code.toString() == Constants.CODE_HTTP_UNAUTHORIZED) {
                            NavigateToActivity.navigateToMainActivity(this@ParametersActivity)
                        }
                    }.show()
                }
            })
    }

    private fun scheduleInquire(result: DataEntity) {
        serviceViewModel.scheduleInquire(sharedHelper?.getUserToken().toString(),
            result.service.id.toString(), result.clientNumber,
            object : OnResponseListener {
                override fun onSuccess(code: Int, msg: String?, obj: Any?) {
//                    pDialog.cancel()
                    pDialog.cancel()


                    val builder = AlertDialog.Builder(this@ParametersActivity)
                        .setMessage(resources.getString(R.string.add_to_reminder))
                        .setTitle(resources.getString(R.string.alert))
                        .setCancelable(false)
                        .setPositiveButton(resources.getString(R.string.add))
                        { alertDialog, _ ->
                            alertDialog.cancel()

                            val calendar = Calendar.getInstance()
                            val day = calendar.get(Calendar.DATE).toString()

//                            pDialog.show()
                            pDialog.show()


                            lifecycleScope.launch { scheduleInvoice(result, day) }
                        }
                        .setNegativeButton(resources.getString(R.string.no_add))
                        { alertDialog, _ ->
                            alertDialog.cancel()
                            clearParamsData()

                            ReceiptActivity.getIntent(
                                this@ParametersActivity,
                                result, serviceViewModel.serviceType
                            )

                            Toast.makeText(
                                this@ParametersActivity,
                                "Success",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    val alertDialog = builder.create()
                    alertDialog.show()
                }

                override fun onFailed(code: Int, msg: String?) {
//                    pDialog.cancel()
                    pDialog.cancel()

                    clearParamsData()

                    ReceiptActivity.getIntent(
                        this@ParametersActivity,
                        result, serviceViewModel.serviceType
                    )

                    Toast.makeText(
                        this@ParametersActivity,
                        "Success",
                        Toast.LENGTH_SHORT
                    ).show()

                    //  finish()
                }

            })
    }

    private fun scheduleInvoice(result: DataEntity, day: String) {
        serviceViewModel.scheduleInvoice(sharedHelper?.getUserToken().toString(),
            result.service.id.toString(),
            day, result.clientNumber,
            object : OnResponseListener {
                override fun onSuccess(
                    code: Int,
                    msg: String?,
                    obj: Any?
                ) {
//                    pDialog.cancel()
                    pDialog.cancel()

                    dialog.showSuccessDialog(msg, resources.getString(R.string.app__ok))
                    {
                        dialog.cancel()

                        ReceiptActivity.getIntent(
                            this@ParametersActivity,
                            result, serviceViewModel.serviceType
                        )

                        Toast.makeText(
                            this@ParametersActivity,
                            "Success",
                            Toast.LENGTH_SHORT
                        ).show()

                        //  finish()
                    }
                    dialog.show()
                }

                override fun onFailed(code: Int, msg: String?) {
//                    pDialog.cancel()
                    pDialog.cancel()

                    clearParamsData()

                    dialog.showErrorDialogWithAction(
                        msg, resources.getString(R.string.app__ok)
                    ) {
                        dialog.cancel()

                        ReceiptActivity.getIntent(
                            this@ParametersActivity,
                            result, serviceViewModel.serviceType
                        )

                        Toast.makeText(
                            this@ParametersActivity,
                            "Success",
                            Toast.LENGTH_SHORT
                        ).show()

                        //  finish()

                    }.show()
                }

            })
    }

    private fun sendParametersToAPI() {
        val paymentPojoModel = PaymentPojoModel(
            Constants.IMEI,
            "", serviceViewModel.servicesId,
            amount, paramsArrayListToSend
        )

        inquire(paymentPojoModel)

    }

    private fun inquire(paymentPojoModel: PaymentPojoModel) {
//        pDialog.show()
        pDialog.show()


        serviceViewModel.inquire(sharedHelper?.getUserToken().toString(),
            paymentPojoModel, object : OnResponseListener {
                override fun onSuccess(code: Int, msg: String?, obj: Any?) {
//                    pDialog.cancel()
                    pDialog.cancel()

                    clearParamsData()

                    // Open inquire activity
                    InquireActivity.getIntent(
                        serviceId = paymentPojoModel.serviceId,
                        serviceType = serviceViewModel.serviceType,
                        acceptCheckIntegrationProviderStatus = serviceViewModel.acceptCheckIntegrationProviderStatus,
                        image = serviceViewModel.image.toString(),
                        acceptAmountChange = serviceViewModel.acceptAmountChange,
                        nameAr = serviceViewModel.serviceName.toString(),
                        nameEn = serviceViewModel.serviceName.toString(),
                        serviceViewModel.providerName.toString(),
                        data = obj as DataEntity,
                        paymentPojoModel = paymentPojoModel,
                        serviceTypeCode = serviceViewModel.serviceTypeCode,
                        activity = this@ParametersActivity,
                        serviceAmountInput = serviceViewModel.acceptAmountChange
                    )
                }

                override fun onFailed(code: Int, msg: String?) {
                    showErrorDialog(msg, code)
                }
            })
    }

    private fun showErrorDialog(msg: String?, code: Int?) {
//        pDialog.cancel()
        pDialog.cancel()

        dialog.showErrorDialogWithAction(
            msg, resources.getString(R.string.app__ok)
        ) {
            dialog.cancel()

            if (code.toString() == Constants.CODE_UNAUTH ||
                code.toString() == Constants.CODE_HTTP_UNAUTHORIZED
            ) {
                NavigateToActivity.navigateToMainActivity(this@ParametersActivity)
            }
        }.show()
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
                    cursor?.getString(it)?.replace(
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
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun replaceData(parameters: List<ParametersData>) {
        ui.lytDynamic.removeAllViewsInLayout()

        this.parametersList = parameters

        for (i in parametersList.indices) {
            // Parameter details
            val internalId: String = this.parametersList[i].internal_id
            val type: Int = this.parametersList[i].type
            val paramNameAr: String = this.parametersList[i].name_ar
            val paramNameEn: String = this.parametersList[i].name_en
            val isClientNum: Int = this.parametersList[i].is_client_number
            val display: String = this.parametersList[i].display.toString()

            val paramName = if (Constants.LANG == Constants.AR) paramNameAr else paramNameEn

            val values = arrayListOf<SpinnerModel>()

            for (ii in parametersList[i].type_values.indices) {
                values.add(
                    SpinnerModel(
                        parametersList[i].type_values[ii].value,
                        parametersList[i].type_values[ii].name_ar,
                        parametersList[i].type_values[ii].name_en
                    )
                )
            }

            if (serviceViewModel.serviceType == Constants.INQUIRY_PAYMENT) {
                if (display == Constants.DISPLAY_FOR_ALL || display == Constants.DISPLAY_FOR_INQUIRY) {
                    when (type) {
                        Constants.Number -> {
                            // adding title
                            dynamicLayout!!.addTextViews(
                                ui.lytDynamic,  /*"\u2022 " +*/
                                paramName
                            )
                            // adding view
                            dynamicLayout!!.addViews(ui.lytDynamic, 0, 0, 2)
                            // adding edit text
                            dynamicLayout!!.addEditTexts(
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

                            if (isClientNum == 1) {
                                if (lang == Constants.AR) {
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
                                edtNumber.setOnTouchListener { v: View?, event: MotionEvent ->
                                    val DRAWABLE_LEFT = 0
                                    val DRAWABLE_RIGHT = 2
                                    if (event.action == MotionEvent.ACTION_UP) {
                                        if (lang.equals(Constants.AR)) {
                                            if (event.rawX <= edtNumber.compoundDrawables[DRAWABLE_LEFT]
                                                    .bounds.width() * 3
                                            ) {
                                                // your action here
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    val result: Boolean =
                                                        PermissionHelper.checkSinglePermission(
                                                            this,
                                                            Manifest.permission.READ_CONTACTS,
                                                            PICK_CONTACT_REQUESTCODE
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
                                                            PICK_CONTACT_REQUESTCODE
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
                        }

                        Constants.Char -> {
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
                                1
                            ) { value -> }

                            val edtChar: EditText =
                                ui.lytDynamic.findViewWithTag(internalId)
                            edtChar.setText("")
                            // println("edtChar.getId():-> " + edtChar.tag + " : " + internalId)
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
                            ) {

                            }
                            // get created dynamic editText by id
                            val etDateTime: EditText =
                                ui.lytDynamic.findViewWithTag(internalId)

                            etDateTime.setOnClickListener {
                                TimeDialogs.openDateCalender(
                                    this@ParametersActivity,
                                    etDateTime
                                )
                            }
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
                            ) {

                            }

                            val edtTxtArea: EditText = ui.lytDynamic.findViewWithTag(internalId)
                            edtTxtArea.setText("")
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

                            dynamicLayout?.addSpinners(
                                ui.lytDynamic, internalId, 0, "\u2022 " +
                                        paramName + " ", values
                            ) { value -> paramsArrayListToSend.add(Params(internalId, value)) }

                            val spinner = ui.lytDynamic.findViewWithTag<Spinner>(internalId)

                        }

                        Constants.Radio -> {
                            dynamicLayout?.addTextViews(ui.lytDynamic, paramName)

                            dynamicLayout?.addViews(ui.lytDynamic, 0, 0, 2)

                            dynamicLayout?.addRadioButtons(ui.lytDynamic, internalId, values)
                        }
                    }
                }
            } else if (serviceViewModel.serviceType == Constants.PAYMENT) {
                Log.d(TAG, "diaa replace data type $type")

                if (display == Constants.DISPLAY_FOR_ALL
                    || display == Constants.DISPLAY_FOR_PAYMENT
                ) {
                    when (type) {
                        Constants.Number -> {
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
                                InputType.TYPE_CLASS_NUMBER,
                                1
                            ) { }

                            val edtNumber: EditText =
                                ui.lytDynamic.findViewWithTag(internalId)
                            edtNumber.setText("")

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
                                    val DRAWABLE_RIGHT = 2
                                    if (event.action == MotionEvent.ACTION_UP) {
                                        if (lang.equals(Constants.AR)) {
                                            Log.d(TAG, "diaa replaceData if condition arabic: ")
                                            Log.d(TAG, "diaa replace data: event ${event.rawX}")
                                            Log.d(
                                                TAG, "diaa replace data: event new ${
                                                    (edtNumber.compoundDrawables[DRAWABLE_LEFT]
                                                        .bounds.width() * 3)
                                                }"
                                            )

                                            if (event.rawX <= edtNumber.compoundDrawables[DRAWABLE_LEFT]
                                                    .bounds.width() * 3
                                            ) {
                                                Log.d(
                                                    TAG,
                                                    "diaa replaceData if condition arabic event: "
                                                )
                                                // your action here
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    val result =
                                                        PermissionHelper.checkSinglePermission(
                                                            this,
                                                            Manifest.permission.READ_CONTACTS,
                                                            PICK_CONTACT_REQUESTCODE
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
                                            Log.d(TAG, "diaa replaceData if condition english: ")

                                            if (event.rawX >= edtNumber.right - edtNumber.compoundDrawables[DRAWABLE_RIGHT].bounds.width() * 2
                                            ) {
                                                Log.d(
                                                    TAG,
                                                    "diaa replaceData if condition english event: "
                                                )

                                                // your action here
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    val result =
                                                        PermissionHelper.checkSinglePermission(
                                                            this,
                                                            Manifest.permission.READ_CONTACTS,
                                                            PICK_CONTACT_REQUESTCODE
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
                        }

                        Constants.Char -> {
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
                                1
                            ) { }

                            val edtChar: EditText =
                                ui.lytDynamic.findViewWithTag(internalId)
                            edtChar.setText("")
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
                                    Params(
                                        internalId,
                                        value
                                    )
                                )
                            }

                            val spinner: Spinner =
                                ui.lytDynamic.findViewWithTag(internalId)
                            //spinner.setSelection();
                        }

                        Constants.Radio -> {
                            dynamicLayout?.addTextViews(
                                ui.lytDynamic,  /*"\u2022 " +*/
                                paramName
                            )
                            // adding view
                            dynamicLayout?.addViews(ui.lytDynamic, 0, 0, 2)
                            // dynamicLayout.addLineSeperator(lytDynamic);
                            dynamicLayout?.addRadioButtons(
                                ui.lytDynamic,
                                internalId,
                                values
                            )
                        }
                    }
                }
            }

            // adding view
            dynamicLayout?.addViews(ui.lytDynamic, 0, 0, 2)
        }

        if (serviceViewModel.acceptAmountinput == 1) {
            // adding title
            dynamicLayout?.addTextViews(
                ui.lytDynamic,  /*"\u2022 " +*/
                getString(R.string.params_amount) + " "
            )
            // adding view
            dynamicLayout?.addViews(ui.lytDynamic, 0, 0, 2)
            // adding edit text
            dynamicLayout?.addEditTexts(
                ui.lytDynamic,
                true,
                "amount",
                getString(R.string.params_amount),
                InputType.TYPE_CLASS_NUMBER,
                1
            ) { }

            val edtAmount: EditText = ui.lytDynamic.findViewWithTag("amount")
            edtAmount.inputType =
                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
            edtAmount.setText("")
        }

        // adding view
        dynamicLayout?.addViews(ui.lytDynamic, 0, 0, 10)
    }

    private fun getIntentData() {
        try {
            if (intent.extras != null) {
                serviceViewModel.servicesId = intent.getIntExtra(Constants.SERVICE_ID, 0)
                serviceViewModel.serviceType = intent.getIntExtra(Constants.SERVICE_TYPE, 0)
                serviceViewModel.serviceName = intent.getStringExtra(Constants.SERVICE_NAME_AR)
                serviceViewModel.serviceNameEN = intent.getStringExtra(Constants.SERVICE_NAME_EN)
                serviceViewModel.providerName = intent.getStringExtra(Constants.PROVIDER_NAME)
                serviceViewModel.priceType = intent.getIntExtra(Constants.PRICE_TYPE, 0)
                serviceViewModel.priceValue = intent.getStringExtra(Constants.PRICE_VALUE)
                serviceViewModel.acceptAmountChange =
                    intent.getIntExtra(Constants.ACCEPT_AMOUNT_CHANGE, 0)
                serviceViewModel.image = intent.getStringExtra(Constants.IMAGE)
                serviceViewModel.acceptAmountinput =
                    intent.getIntExtra(Constants.ACCEPT_AMOUNT_INPUT, 0)

            }
        } catch (e: Exception) {
            Log.d(TAG, "getIntentData catch: ${e.message}")
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


    private fun getParamsData(): Boolean {
        for (i in this.parametersList.indices) {
            val internalId: String = this.parametersList[i].internal_id
            val type: Int = this.parametersList[i].type
            val minLenght: Int = this.parametersList[i].min_length
            val maxLenght: Int = this.parametersList[i].max_length
            val required: Int = this.parametersList[i].required
            val display: String = this.parametersList[i].display.toString()
            var shouldBreak = false
            val values = ArrayList<SpinnerModel>()

            for (ii in 0 until parametersList[i].type_values.size) {
                values.add(
                    SpinnerModel(
                        parametersList[i].type_values[ii].value,
                        parametersList[i].type_values[ii].name_ar,
                        parametersList[i].type_values[ii].name_en
                    )
                )
            }
            if (serviceViewModel.serviceType == Constants.INQUIRY_PAYMENT) {
                if (display == Constants.DISPLAY_FOR_ALL || display == Constants.DISPLAY_FOR_INQUIRY) {
                    when (type) {
                        Constants.Number -> {
                            val etNumber: EditText =
                                ui.lytDynamic.findViewWithTag(internalId)
                            val valueNumber = Utils.replaceArabicNumbers(etNumber.text.toString())
                            if (required == 1 && valueNumber.isEmpty()) {
                                etNumber.error = getString(R.string.required)
                                shouldBreak = true
                            } else if (valueNumber.length < minLenght || valueNumber.length > maxLenght) {
                                if (minLenght == maxLenght) {
                                    etNumber.error =
                                        getString(R.string.required_value_number) + " " + minLenght + getString(
                                            R.string.number
                                        )
                                } else {
                                    etNumber.error =
                                        getString(R.string.required_value_range) + " " + minLenght + " " + getString(
                                            R.string.number
                                        ) + getString(R.string.and) + " " + getString(R.string.number) + maxLenght
                                }
                                shouldBreak = true
                            }
                            paramsArrayListToSend.add(Params(internalId, valueNumber))
                        }

                        Constants.Char -> {
                            val etChar: EditText =
                                ui.lytDynamic.findViewWithTag(internalId)
                            val value = etChar.text.toString()
                            if (required == 1 && value.isEmpty()) {
                                etChar.error = getString(R.string.required)
                                shouldBreak = true
                            }
                            paramsArrayListToSend.add(Params(internalId, value))
                        }

                        Constants.Date -> {
                            val etDate: EditText =
                                ui.lytDynamic.findViewWithTag(internalId)
                            val etDateValue = Utils.replaceArabicNumbers(etDate.text.toString())
                            if (required == 1 && etDateValue.isEmpty()) {
                                etDate.error = getString(R.string.required)
                                shouldBreak = true
                            }
                            paramsArrayListToSend.add(Params(internalId, etDateValue))
                        }

                        Constants.textarea -> {
                            val etTextArea: EditText =
                                ui.lytDynamic.findViewWithTag(internalId)
                            val valueTxtArea = etTextArea.text.toString()
                            if (required == 1 && valueTxtArea.isEmpty()) {
                                etTextArea.error = getString(R.string.required)
                                shouldBreak = true
                            }
                            paramsArrayListToSend.add(Params(internalId, valueTxtArea))
                        }

                        Constants.Select -> {
                            val spinner: Spinner =
                                ui.lytDynamic.findViewWithTag(internalId)
                            val spinnerSelectedName = spinner.selectedItem.toString()
                            if (required == 1 && spinnerSelectedName.lowercase(Locale.getDefault())
                                    .contains("select")
                            ) {
                                showMessage(internalId + getString(R.string.required))
                                shouldBreak = true
                            }
                            var iii = 0
                            while (iii < values.size) {
                                if (spinnerSelectedName == values[iii].getaName() ||
                                    spinnerSelectedName == values[iii].geteName()
                                ) {
                                    paramsArrayListToSend.add(Params(internalId, values[iii].id))
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
                            val radioButton =
                                radioGroup.findViewById<View>(selectedId) as RadioButton
                            if (required == 1) {
                                if (!radioButton.isChecked) {
                                    showMessage(internalId + getString(R.string.required))
                                    shouldBreak = true
                                } else {
                                    val radiovalue = radioButton.text.toString()
                                    var iii = 0
                                    while (iii < values.size) {
                                        if (radiovalue == values[iii].getaName() ||
                                            radiovalue == values[iii].geteName()
                                        ) {
                                            paramsArrayListToSend.add(
                                                Params(
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
            } else if (serviceViewModel.serviceType == Constants.PAYMENT) {
                if (display == Constants.DISPLAY_FOR_ALL || display == Constants.DISPLAY_FOR_PAYMENT) {
                    when (type) {
                        Constants.Number -> {
                            val etNumber: EditText =
                                ui.lytDynamic.findViewWithTag(internalId)
                            val valueNumber = Utils.replaceArabicNumbers(etNumber.text.toString())
                            if (required == 1 && valueNumber.isEmpty()) {
                                etNumber.error = getString(R.string.required)
                                shouldBreak = true
                            } else if (valueNumber.length < minLenght || valueNumber.length > maxLenght) {
                                if (minLenght == maxLenght) {
                                    etNumber.error =
                                        getString(R.string.required_value_number) + " " + minLenght + getString(
                                            R.string.number
                                        )
                                } else {
                                    etNumber.error =
                                        getString(R.string.required_value_range) + " " + minLenght + " " + getString(
                                            R.string.number
                                        ) + getString(R.string.and) + " " + getString(R.string.number) + maxLenght
                                }
                                shouldBreak = true
                            }
                            paramsArrayListToSend.add(Params(internalId, valueNumber))
                        }

                        Constants.Char -> {
                            val etChar: EditText =
                                ui.lytDynamic.findViewWithTag(internalId)
                            val value = etChar.text.toString()
                            if (required == 1 && value.isEmpty()) {
                                etChar.error = getString(R.string.required)
                                shouldBreak = true
                            }
                            paramsArrayListToSend.add(Params(internalId, value))
                        }

                        Constants.Date -> {
                            val etDate: EditText =
                                ui.lytDynamic.findViewWithTag(internalId)
                            val etDateValue = Utils.replaceArabicNumbers(etDate.text.toString())
                            if (required == 1 && etDateValue.isEmpty()) {
                                etDate.error = getString(R.string.required)
                                shouldBreak = true
                            }
                            paramsArrayListToSend.add(Params(internalId, etDateValue))
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
                                Params(
                                    internalId,
                                    valueTxtArea
                                )
                            )
                        }

                        Constants.Select -> {
                            val spinner: Spinner =
                                ui.lytDynamic.findViewWithTag(internalId)
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
                                        Params(
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
                            val radioButton =
                                radioGroup.findViewById<View>(selectedId) as RadioButton
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
                                                Params(
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
            }
            if (shouldBreak) return false
        }
        return true
    }

    private fun clearParamsData() {
        Log.d("TAG", "diaa clearParamsData: ${serviceViewModel.acceptAmountinput}")

        if (serviceViewModel.acceptAmountinput == 1) {
            val edtAmount: EditText = ui.lytDynamic.findViewWithTag("amount")
            edtAmount.setText("")
        }

        // get Params
        for (i in this.parametersList.indices) {

            // Params Details
            val internalId: String = this.parametersList[i].internal_id
            val type: Int = this.parametersList[i].type
            val paramNameAr: String = this.parametersList[i].name_ar
            val display: String = this.parametersList[i].display.toString()
            val values = ArrayList<SpinnerModel>()
            for (ii in 0 until parametersList.get(i).type_values.size) {
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

            //add header item in spinner list
            values.add(SpinnerModel("0", "أختر $paramNameAr", "Choose$paramNameAr"))

            if (serviceViewModel.serviceType == Constants.INQUIRY_PAYMENT) {
                if (display == Constants.DISPLAY_FOR_ALL || display == Constants.DISPLAY_FOR_INQUIRY) {
                    when (type) {
                        Constants.Number -> {
                            val etNumber: EditText =
                                ui.lytDynamic.findViewWithTag(internalId)
                            etNumber.setText("")
                        }

                        Constants.Char -> {
                            val etChar: EditText =
                                ui.lytDynamic.findViewWithTag(internalId)
                            etChar.setText("")
                        }

                        Constants.Date -> {
                            val etDate: EditText =
                                ui.lytDynamic.findViewWithTag(internalId)
                            etDate.setText("")
                        }

                        Constants.textarea -> {
                            val etTextArea: EditText =
                                ui.lytDynamic.findViewWithTag(internalId)
                            etTextArea.setText("")
                        }

                        Constants.Select -> {
                            val spinner: Spinner =
                                ui.lytDynamic.findViewWithTag(internalId)
                            spinner.setSelection(0)
                        }

                        Constants.Radio -> {
                            val radioGroup: RadioGroup =
                                ui.lytDynamic.findViewWithTag(internalId)
                            radioGroup.clearCheck()
                        }

                        else -> {}
                    }
                }
            } else if (serviceViewModel.serviceType == Constants.PAYMENT) {
                if (display == Constants.DISPLAY_FOR_ALL || display == Constants.DISPLAY_FOR_PAYMENT) {
                    when (type) {
                        Constants.Number -> {
                            val etNumber: EditText =
                                ui.lytDynamic.findViewWithTag(internalId)
                            etNumber.setText("")
                        }

                        Constants.Char -> {
                            val etChar: EditText =
                                ui.lytDynamic.findViewWithTag(internalId)
                            etChar.setText("")
                        }

                        Constants.Date -> {
                            val etDate: EditText =
                                ui.lytDynamic.findViewWithTag(internalId)
                            etDate.setText("")
                        }

                        Constants.textarea -> {
                            val etTextArea: EditText =
                                ui.lytDynamic.findViewWithTag(internalId)
                            etTextArea.setText("")
                        }

                        Constants.Select -> {
                            val spinner: Spinner =
                                ui.lytDynamic.findViewWithTag(internalId)
                            spinner.setSelection(0)
                        }

                        Constants.Radio -> {
                            val radioGroup: RadioGroup =
                                ui.lytDynamic.findViewWithTag(internalId)
                            radioGroup.clearCheck()
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    //endregion
    private fun showMessage(message: String?) {
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, getString(R.string.some_error), Toast.LENGTH_SHORT).show()
        }
    }
}