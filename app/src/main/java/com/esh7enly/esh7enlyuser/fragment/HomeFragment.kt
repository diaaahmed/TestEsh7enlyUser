package com.esh7enly.esh7enlyuser.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.esh7enly.domain.entity.categoriesNew.CategoryData
import com.esh7enly.domain.entity.imageadsresponse.Data
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.activity.BaseFragment
import com.esh7enly.esh7enlyuser.activity.SearchActivity
import com.esh7enly.esh7enlyuser.adapter.CategoryAdapterNew
import com.esh7enly.esh7enlyuser.adapter.FlipperAdapter
import com.esh7enly.esh7enlyuser.adapter.ImageAdsAdapter
import com.esh7enly.esh7enlyuser.click.CategoryClick
import com.esh7enly.esh7enlyuser.click.OnResponseListener
import com.esh7enly.esh7enlyuser.databinding.FragmentHomeBinding
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.Language
import com.esh7enly.esh7enlyuser.util.NavigateToActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs

private const val TAG = "HomeFragment"

@AndroidEntryPoint
class HomeFragment : BaseFragment(), CategoryClick {

    var categories:List<CategoryData> ?= null


    private var imageAdsAdapter: ImageAdsAdapter? = null

    private val ui by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }

    lateinit var categoriesAdapter: CategoryAdapterNew

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return ui.root
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.HONEYCOMB)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Language.setLanguageNew(requireActivity(), Constants.LANG)


        Log.d(TAG, "diaa onViewCreated: ")

        ui.servicesRv.setHasFixedSize(true)

//        pDialog.setMessage(
//            Utils.getSpannableString(
//                requireActivity(),
//                resources.getString(R.string.message__please_wait)
//            )
//        )
//        pDialog.setCancelable(false)

        ui.txtWelcome.text =
            resources.getString(R.string.welcome) + " " + sharedHelper?.getStoreName()

        askNotificationPermission()

        getData()

        ui.searchBtn.setOnClickListener { serviceSearch() }

    }


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        {
            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS))
            {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)

            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }


    override fun onPause() {
        super.onPause()
        ui.shimmerViewContainer.stopShimmerAnimation()
        Log.d(TAG, "diaa onPause: ")
    }

    private fun serviceSearch() {
        val serviceSearch = ui.searchWord.text.toString()

        if (serviceSearch.isBlank())
        {
            ui.searchWord.error = resources.getString(R.string.required)
        } else {

            val searchActivity = Intent(requireContext(), SearchActivity::class.java)
            searchActivity.putExtra(Constants.SERVICE_NAME, serviceSearch)
            startActivity(searchActivity)
        }
    }

    private fun checkCancelTransactions() {
        // serviceViewModel.clearFawryOperations()

        serviceViewModel.getFawryOperations().observe(requireActivity())
        { fawryEntities ->

            Log.d(TAG, "diaa fawry entities: $fawryEntities")

            if (fawryEntities.isNotEmpty()) {
                Log.d(TAG, "diaa fawry entities size: ${fawryEntities.size}")

                for (i in fawryEntities.indices) {
                    Log.d(TAG, "diaa fawry entities information: ${fawryEntities[i].id}")
                    Log.d(TAG, "diaa fawry entities information: ${fawryEntities[i].imie}")
                    Log.d(TAG, "diaa fawry entities information: ${fawryEntities[i].date}")

                    cancelTransaction(
                        fawryEntities[i].id.toString(),
                        fawryEntities[i].imie
                    )
                }
            } else {
                Log.d(TAG, "diaa fawry entities: No data")
            }
        }
    }

    private fun cancelTransaction(transactionId: String, imei: String) {
        serviceViewModel.cancelTransaction(sharedHelper?.getUserToken().toString(),
            transactionId,
            imei, object : OnResponseListener {
                override fun onSuccess(code: Int, msg: String?, obj: Any?) {
                    serviceViewModel.deleteFawryOperations(transactionId.toInt())

                    showMessage(" تم استرجاع المبلغ اليكم $msg ")
                }

                override fun onFailed(code: Int, msg: String?) {
                    serviceViewModel.deleteFawryOperations(transactionId.toInt())
                    //  showMessage(" فشل استرجاع المبلغ$msg")
                    Log.d(TAG, "diaa on cancel failed: $msg")
                }
            })
    }

    private fun showMessage(message: String?) {

        lifecycleScope.launch(Dispatchers.Main)
        {
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    requireContext(), R.string.some_error,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        ui.servicesRv.adapter = null
        Log.d(TAG, "diaa onDestroy: ")
    }


    @RequiresApi(Build.VERSION_CODES.HONEYCOMB)
    private fun getData()
    {
        getDataFromServer()
//        lifecycleScope.launch(Dispatchers.IO)
//        {
//
//            getDataFromServer()
//
////            val number: String = serviceViewModel.getDbVersion()
////
////            Log.d(
////                "TAG", "diaa get db version: " +
////                        "$number Constant ${Constants.SERVICE_UPDATE_NUMBER}"
////            )
////
////            if(number != Constants.SERVICE_UPDATE_NUMBER)
////            {
////                if(connectivity?.isConnected == true)
////                {
////                    getDataFromServer()
////                }
////            }
////            else
////            {
////               getDataFromDB()
////            }
//        }

        if(connectivity?.isConnected == true)
        {
            checkCancelTransactions()
            addDynamicAdds()

        }
        addFixedAds()
    }

    private fun addDynamicAdds() {
        serviceViewModel.getImageAds(sharedHelper?.getUserToken().toString(),
            object : OnResponseListener {
                @RequiresApi(Build.VERSION_CODES.HONEYCOMB)
                override fun onSuccess(code: Int, msg: String?, obj: Any?)
                {
                    val data = obj as Data

                    imageAdsAdapter = ImageAdsAdapter(data.data)

                    Handler().postDelayed({
                        ui.adsViewPager.setCurrentItem(2, true)
                    }, 10)

                    ui.adsViewPager.offscreenPageLimit = 3
                    ui.adsViewPager.clipChildren = false
                    ui.adsViewPager.clipToPadding = false
                    ui.adsViewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
                    val transformer = CompositePageTransformer()
                    transformer.addTransformer(MarginPageTransformer(40))
                    transformer.addTransformer { page, position ->
                        val r = 1 - abs(position)
                        page.scaleY = 0.85f + r * 0.14f
                    }

                    ui.adsViewPager.setPageTransformer(transformer)
                    ui.adsViewPager.adapter = imageAdsAdapter
                }

                override fun onFailed(code: Int, msg: String?)
                {
                    Log.d(TAG, "diaa getImageAds Error: $msg")
                }
            })
    }

    private fun addFixedAds()
    {
        val appAds =
            mutableListOf(R.drawable.forsa, R.drawable.adone, R.drawable.adtwo, R.drawable.adthree)

        val flipAdsAdapter = FlipperAdapter(requireActivity(), appAds, ui.esh7enlyAds)
        ui.esh7enlyAds.adapter = flipAdsAdapter
    }

//    private fun getDataFromDB()
//    {
//        Log.d(TAG, "diaa getDataFromDB: ")
//
//        lifecycleScope.launch(Dispatchers.IO) {
//           // val filteredCategory = serviceViewModel.getFilteredList()
//            val filteredCategory = serviceViewModel.getFilteredList()
//            replaceData(filteredCategory)
//        }
//
//        ui.shimmerViewContainer.stopShimmerAnimation()
//        ui.shimmerViewContainer.visibility = View.GONE
//        ui.servicesRv.visibility = View.VISIBLE
//
//    }

    private fun getDataFromServer()
    {
        Log.d(TAG, "diaa getDataFromServer: ")

//        lifecycleScope.launch(Dispatchers.IO) {

            ui.shimmerViewContainer.startShimmerAnimation()

            serviceViewModel.getCategories(sharedHelper?.getUserToken().toString(),
                object : OnResponseListener {
                    override fun onSuccess(code: Int, msg: String?, obj: Any?)
                    {
                         categories = obj as List<CategoryData>

                        val other = CategoryData(
                            name_ar = "خدمات أخرى",
                            name_en = "Other Service",
                            id = 0
                        )

                        val filteredCategory = listOf(categories!![0],categories!![1],other)

                        replaceData(filteredCategory)

//                        lifecycleScope.launch(Dispatchers.IO) {
//                            // val filteredCategory = serviceViewModel.getFilteredList()
//                            val filteredCategory = serviceViewModel.getFilteredList()
//                            replaceData(filteredCategory)
//                        }

                        ui.shimmerViewContainer.stopShimmerAnimation()
                        ui.shimmerViewContainer.visibility = View.GONE
                        ui.servicesRv.visibility = View.VISIBLE
                    }

                    override fun onFailed(code: Int, msg: String?)
                    {
                        Log.d(TAG, "onFailed: ")
                    }
                })
        //}

//        serviceViewModel.getService(sharedHelper?.getUserToken().toString())
//
//        lifecycleScope.launch(Dispatchers.Main)
//        {
//            serviceViewModel._dataStatus.observe(requireActivity())
//            {
//                when (it) {
//                    ServiceStatus.SUCCESS -> {
//                        Log.d(TAG, "diaa here call")
//
//                        lifecycleScope.launch(Dispatchers.Main)
//                        {
//                            getDataFromDB()
//                        }
//                    }
//                    ServiceStatus.ERROR -> {
//                        dialog.showErrorDialogWithAction(resources.getString(R.string.error),resources.getString(R.string.app__ok)
//                        ) {
//                            dialog.cancel()
//
//                            NavigateToActivity.navigateToMainActivity(requireActivity())
//
//                        }.show()
//                    }
//                    else -> {
//                        ui.shimmerViewContainer.startShimmerAnimation()
//                        Log.d(TAG, "diaa dataFrom service loading: ")
//                    }
//                }
//            }
//        }
    }


    private fun replaceData(category: List<CategoryData>)
    {
        lifecycleScope.launch(Dispatchers.Main)
        {
            categoriesAdapter = CategoryAdapterNew(category,this@HomeFragment)
            ui.servicesRv.adapter = categoriesAdapter
        }
    }

    override fun click(category: CategoryData)
    {
        if (category.id == 0)
        {

            replaceData(categories!!)

//            lifecycleScope.launch(Dispatchers.IO) {
//                val allCategories = serviceViewModel.getAllCategories()
//                withContext(Dispatchers.Main)
//                {
//                    replaceData(allCategories)
//
//                }
//            }
        }
        else
        {
            NavigateToActivity.navigateToProviderActivity(requireActivity(),category)
        }
    }
}