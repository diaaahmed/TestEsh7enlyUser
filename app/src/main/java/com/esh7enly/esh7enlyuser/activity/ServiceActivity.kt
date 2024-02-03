package com.esh7enly.esh7enlyuser.activity

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope

import com.esh7enly.domain.entity.TotalAmountPojoModel
import com.esh7enly.domain.entity.userservices.*
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.adapter.ServiceAdapter
import com.esh7enly.esh7enlyuser.click.ServiceClick
import com.esh7enly.esh7enlyuser.databinding.ActivityServiceBinding
import com.esh7enly.esh7enlyuser.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "ServiceActivity"

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

    private var provider_id = 0
    lateinit var providerId: String
    lateinit var providerName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)

        initToolBar()

        ui.serviceRv.setHasFixedSize(true)

        provider_id = intent.getIntExtra(Constants.PROVIDER_ID, 0)
        providerName = intent.getStringExtra(Constants.PROVIDER_NAME).toString()
        providerId = provider_id.toString()

        getServices()
    }

    override fun initToolBar()
    {
        ui.serviceToolbar.title = intent.getStringExtra(Constants.PROVIDER_NAME) ?: ""

        ui.serviceToolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun getServices() {
        serviceViewModel.getServicesFromDB(providerId).observe(this)
        { services ->
            adapter.submitList(services)
            ui.serviceRv.adapter = adapter
        }
    }


    override fun click(service: Service)
    {
        serviceViewModel.serviceType = service.type

        Log.d(TAG, "diaa test type: ${service.type}")

        if (service.type == Constants.PREPAID_CARD)
        {
            if (connectivity?.isConnected == true) {
                pDialog.show()

                val totalAmountPojoModel =
                    TotalAmountPojoModel(Constants.IMEI, service.id, service.price_value)

                lifecycleScope.launch(Dispatchers.IO) {

                    getTotalAmount(totalAmountPojoModel)
                }
            } else {
                dialog.showWarningDialog(
                    resources.getString(R.string.no_internet_error),
                    resources.getString(R.string.app__ok)
                )
                dialog.show()
            }

        } else
        {
            navigateToParametersActivity(service)
        }
    }

}