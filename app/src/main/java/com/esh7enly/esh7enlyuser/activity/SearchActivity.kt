package com.esh7enly.esh7enlyuser.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.esh7enly.domain.entity.TotalAmountPojoModel
import com.esh7enly.domain.entity.userservices.*
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.adapter.ServiceAdapter
import com.esh7enly.esh7enlyuser.click.ServiceClick
import com.esh7enly.esh7enlyuser.databinding.ActivitySearchBinding
import com.esh7enly.esh7enlyuser.util.AppDialogMsg
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.IToolbarTitle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SearchActivity : BaseActivity(),ServiceClick,IToolbarTitle
{

    private val ui by lazy{ ActivitySearchBinding.inflate(layoutInflater) }

    lateinit var providerName:String

    private val dialog by lazy { AppDialogMsg(this,false) }

    private val adapter by lazy { ServiceAdapter(this) }


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)

        initToolBar()

        providerName = "Empty provider"

        val serviceSearch = intent.getStringExtra(Constants.SERVICE_NAME)

        lifecycleScope.launch {
            serviceViewModel.searchService(serviceSearch!!)
                .observe(this@SearchActivity)
                {
                    if(it.isEmpty())
                    {
                        ui.animationView.visibility = View.VISIBLE
                        ui.searchRv.visibility = View.GONE
                    }
                    else
                    {
                        ui.animationView.visibility = View.GONE
                        ui.searchRv.visibility = View.VISIBLE
                        adapter.submitList(it)
                        ui.searchRv.adapter = adapter
                    }
                }
        }

    }

    override fun initToolBar() {
        ui.resultsToolbar.title = resources.getString(R.string.results)
        ui.resultsToolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    override fun click(service: Service)
    {
        serviceViewModel.serviceType = service.type

        if(service.type == Constants.PREPAID_CARD)
        {
            if(connectivity?.isConnected == true)
            {
                pDialog.show()

                val totalAmountPojoModel = TotalAmountPojoModel(Constants.IMEI,service.id,service.price_value)

                lifecycleScope.launch(Dispatchers.IO) {

                    getTotalAmount(totalAmountPojoModel)
                }
            }
            else
            {
                dialog.showWarningDialog(resources.getString(R.string.no_internet_error),
                    resources.getString(R.string.app__ok))
                dialog.show()
            }
        }

        else
        {
            navigateToParametersActivity(service)
        }
    }

}