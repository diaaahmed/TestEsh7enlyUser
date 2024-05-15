package com.esh7enly.esh7enlyuser.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import com.esh7enly.domain.entity.TotalAmountPojoModel
import com.esh7enly.domain.entity.searchresponse.SearchData
import com.esh7enly.domain.entity.userservices.*
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.adapter.SearchAdapter
import com.esh7enly.esh7enlyuser.click.OnResponseListener
import com.esh7enly.esh7enlyuser.click.SearchClick
import com.esh7enly.esh7enlyuser.databinding.ActivitySearchBinding
import com.esh7enly.esh7enlyuser.util.AppDialogMsg
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.IToolbarTitle
import com.esh7enly.esh7enlyuser.util.Language
import com.esh7enly.esh7enlyuser.util.NavigateToActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SearchActivity : BaseActivity(),SearchClick,IToolbarTitle
{

    private val ui by lazy{ ActivitySearchBinding.inflate(layoutInflater) }

    private var page = 1

    private val dialog by lazy { AppDialogMsg(this,false) }

    private val adapter by lazy { SearchAdapter(this) }

    private var serviceSearch:String?= null

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)

        Language.setLanguageNew(this, Constants.LANG)

        initToolBar()

        serviceSearch = intent.getStringExtra(Constants.SERVICE_NAME)

        pDialog.show()

       // initRecyclerView()
        serviceSearchRemotely(serviceSearch!!)

    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun initRecyclerView() {
        ui.searchRv.setHasFixedSize(true)

        ui.nestedScrollView.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener
            { v, _, scrollY, _, _ ->
                if(scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight)
                {
                    pDialog.show()

                    Language.setLanguageNew(this, Constants.LANG)

                    page++
                    serviceViewModel.serviceSearch(sharedHelper?.getUserToken().toString(),
                        serviceSearch!!,page, object : OnResponseListener {
                            override fun onSuccess(code: Int, msg: String?, obj: Any?) {

                                pDialog.cancel()

                                val serviceData = obj as List<SearchData>

                                if (serviceData.isEmpty()) {
                                    ui.animationView.visibility = View.VISIBLE
                                    ui.searchRv.visibility = View.GONE
                                } else {
                                    ui.animationView.visibility = View.GONE
                                    ui.searchRv.visibility = View.VISIBLE
                                    adapter.submitList(serviceData)
                                    ui.searchRv.adapter = adapter
                                }
                            }

                            override fun onFailed(code: Int, msg: String?) {
                            }
                        })
                }
            })
    }

    private fun serviceSearchRemotely(serviceName:String)
    {
        lifecycleScope.launch {

            serviceViewModel.serviceSearch(sharedHelper?.getUserToken().toString(),
            serviceName,1, object : OnResponseListener {
                    override fun onSuccess(code: Int, msg: String?, obj: Any?)
                    {
                        pDialog.cancel()

                        val serviceData = obj as List<SearchData>

                        if (serviceData.isEmpty()) {
                            ui.animationView.visibility = View.VISIBLE
                            ui.searchRv.visibility = View.GONE
                        } else {
                            ui.animationView.visibility = View.GONE
                            ui.searchRv.visibility = View.VISIBLE
                            adapter.submitList(serviceData)
                            ui.searchRv.adapter = adapter
                        }

                    }

                    override fun onFailed(code: Int, msg: String?)
                    {
                        pDialog.cancel()

                        dialog.showErrorDialogWithAction(
                            msg, resources.getString(R.string.app__ok)
                        ) {
                            dialog.cancel()

                            if (code.toString() == Constants.CODE_UNAUTH ||
                                code.toString() == Constants.CODE_HTTP_UNAUTHORIZED
                            ) {
                                NavigateToActivity.navigateToMainActivity(this@SearchActivity)
                            }
                        }.show()
                    }
                })



//            serviceViewModel.serviceSearch(sharedHelper?.getUserToken().toString(),
//                serviceSearch!!)
//                .observe(this@SearchActivity)
//                {
//                    if (it.isEmpty()) {
//                        ui.animationView.visibility = View.VISIBLE
//                        ui.searchRv.visibility = View.GONE
//                    } else {
//                        ui.animationView.visibility = View.GONE
//                        ui.searchRv.visibility = View.VISIBLE
//                        adapter.submitList(it)
//                        ui.searchRv.adapter = adapter
//                    }
//                }
        }
    }
    private fun serviceSearchFromDB()
    {
        //        lifecycleScope.launch {
//            serviceViewModel.searchService(serviceSearch!!)
//                .observe(this@SearchActivity)
//                {
//                    if(it.isEmpty())
//                    {
//                        ui.animationView.visibility = View.VISIBLE
//                        ui.searchRv.visibility = View.GONE
//                    }
//                    else
//                    {
//                        ui.animationView.visibility = View.GONE
//                        ui.searchRv.visibility = View.VISIBLE
//                        adapter.submitList(it)
//                        ui.searchRv.adapter = adapter
//                    }
//                }
//        }
    }

    override fun initToolBar() {
        ui.resultsToolbar.title = resources.getString(R.string.results)
        ui.resultsToolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    override fun click(service: SearchData)
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