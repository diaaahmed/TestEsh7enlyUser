package com.esh7enly.esh7enlyuser.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.esh7enly.domain.entity.userservices.*
import com.esh7enly.esh7enlyuser.adapter.ProviderAdapter
import com.esh7enly.esh7enlyuser.click.ProviderClick
import com.esh7enly.esh7enlyuser.databinding.ActivityProviderBinding
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.IToolbarTitle
import com.esh7enly.esh7enlyuser.viewModel.ServiceViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProviderActivity : AppCompatActivity(),ProviderClick,IToolbarTitle
{
    private val ui by lazy{
        ActivityProviderBinding.inflate(layoutInflater)
    }

    private val providerAdapter by lazy {
        ProviderAdapter(this)
    }

    private val serviceViewModel:ServiceViewModel by viewModels()

    private var category_id = 0

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)

        initToolBar()

        ui.providerRv.setHasFixedSize(true)

        category_id = intent.getIntExtra(Constants.CATEGORY_ID,0)

        getData()
    }

    override fun initToolBar()
    {
        ui.providerToolbar.title = intent.getStringExtra(Constants.CATEGORY_NAME)?: ""

        ui.providerToolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed()}
    }

    private fun getData()
    {
        serviceViewModel.getProvidersFromDB(category_id).observe(this)
        {
            providers->
            providerAdapter.submitList(providers)

            ui.providerRv.adapter = providerAdapter
        }
//        lifecycleScope.launch(Dispatchers.IO){
//            val response = RoomDatabase.getDatabase(this@ProviderActivity)
//                .providerDao().getProviders(category_id)
//
//            withContext(Dispatchers.Main)
//            {
//                providerAdapter.submitList(response)
//
//                ui.providerRv.adapter = providerAdapter
//            }
//
//        }
    }

    override fun click(provider: Provider)
    {
        val serviceActivity = Intent(this@ProviderActivity,ServiceActivity::class.java)
        serviceActivity.putExtra(Constants.PROVIDER_ID,provider.id)

        if(Constants.LANG == Constants.AR)
        {
            serviceActivity.putExtra(Constants.PROVIDER_NAME,provider.name_ar)
        }
        else
        {
            serviceActivity.putExtra(Constants.PROVIDER_NAME,provider.name_en)
        }
        startActivity(serviceActivity)
    }

}