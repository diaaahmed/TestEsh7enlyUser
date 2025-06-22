package com.esh7enly.esh7enlyuser.activity

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.esh7enly.domain.entity.TotalAmountPojoModel
import com.esh7enly.domain.entity.servicesNew.ServiceData
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.adapter.ServiceAdapter
import com.esh7enly.esh7enlyuser.click.OnResponseListener
import com.esh7enly.esh7enlyuser.click.ServiceClick
import com.esh7enly.esh7enlyuser.databinding.ActivityServiceBinding
import com.esh7enly.esh7enlyuser.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ServiceActivity : BaseActivity(), ServiceClick, IToolbarTitle {

    private val ui by lazy {
        ActivityServiceBinding.inflate(layoutInflater)
    }

    private val adapter by lazy {
        ServiceAdapter(this)
    }

    private val dialog by lazy {
        AppDialogMsg(this, false)
    }

    private var providerID = 0

    private lateinit var providerName: String

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)

        Language.setLanguageNew(this, Constants.LANG)

        initToolBar()

        ui.serviceRv.setHasFixedSize(true)

        providerID = intent.getIntExtra(Constants.PROVIDER_ID, 0)

        providerName = intent.getStringExtra(Constants.PROVIDER_NAME).toString()

        getServices()
    }

    override fun initToolBar() {
        ui.serviceToolbar.title = intent.getStringExtra(Constants.PROVIDER_NAME) ?: ""

        ui.serviceToolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun getServices() {

        pDialog.show()

        serviceViewModel.getServicesNew(
            providerID.toString(), object : OnResponseListener {
                override fun onSuccess(code: Int, msg: String?, obj: Any?) {
                    pDialog.cancel()

                    val services = obj as List<ServiceData>

                    adapter.submitList(services)

                    ui.serviceRv.adapter = adapter
                }

                override fun onFailed(code: Int, msg: String?) {
                    pDialog.cancel()

                    dialog.showErrorDialogWithAction(
                        msg, resources.getString(R.string.app__ok)
                    ) {
                        dialog.cancel()

                        if (code == Constants.CODE_UNAUTHENTIC_NEW ||
                            code.toString() == Constants.CODE_HTTP_UNAUTHORIZED
                        ) {
                            NavigateToActivity.navigateToAuthActivity(this@ServiceActivity)
                        }
                    }.show()
                }
            })

    }

    override fun click(service: ServiceData) {
        serviceViewModel.serviceType = service.type

        Constants.SERVICE_TYPE_TEST = service.type

        if (service.type == Constants.PREPAID_CARD) {

            pDialog.show()

            val totalAmountPojoModel =
                TotalAmountPojoModel(Constants.IMEI, service.id, service.priceValue)

            lifecycleScope.launch(Dispatchers.IO) {

                getTotalAmount(
                    totalAmountPojoModel =totalAmountPojoModel,
                    serviceName = service.name,
                    providerName = providerName,
                    serviceIcon = service.icon
                )
            }

        } else {

            NavigateToActivity.navigateToParametersActivity(
                activity = this,
                service = service,
                providerName = providerName,
            )
        }
    }
}