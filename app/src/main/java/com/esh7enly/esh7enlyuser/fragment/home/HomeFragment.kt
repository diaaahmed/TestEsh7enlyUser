package com.esh7enly.esh7enlyuser.fragment.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.esh7enly.domain.NetworkResult
import com.esh7enly.domain.entity.categoriesNew.CategoryData
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
import com.esh7enly.esh7enlyuser.viewModel.ServiceViewModel


import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import kotlin.math.abs

private const val TAG = "HomeFragment"

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding,
        ServiceViewModel>(), CategoryClick {

    private var categories: List<CategoryData>? = null

    private var imageAdsAdapter: ImageAdsAdapter? = null

    private lateinit var categoriesAdapter: CategoryAdapterNew

    override val viewModel: ServiceViewModel by viewModels()

    override fun getLayoutResID() = R.layout.fragment_home

    @RequiresApi(Build.VERSION_CODES.O)
    override fun init() {

        Language.setLanguageNew(requireActivity(), Constants.LANG)

        binding.servicesRv.setHasFixedSize(true)

        binding.txtWelcome.text =
            resources.getString(R.string.welcome) + " " + sharedHelper?.getStoreName()

        askNotificationPermission()

        getData()

        //   binding.searchBtn.setOnClickListener { serviceSearch() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123) {
            if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
            }
        }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)

            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }


    override fun onPause() {
        super.onPause()
        binding.shimmerViewContainer.stopShimmerAnimation()
        Log.d(TAG, "diaa onPause: ")
    }

    private fun serviceSearch() {
        val serviceSearch = binding.searchWord.text.toString()

        if (serviceSearch.isBlank()) {
            binding.searchWord.error = resources.getString(R.string.required)
        } else {

            val searchActivity = Intent(requireContext(), SearchActivity::class.java)
            searchActivity.putExtra(Constants.SERVICE_NAME, serviceSearch)
            startActivity(searchActivity)
        }
    }

    private fun checkCancelTransactions() {
        // serviceViewModel.clearFawryOperations()

        viewModel.getFawryOperations().observe(requireActivity())
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
        viewModel.cancelTransaction(
            transactionId,
            imei, object : OnResponseListener {
                override fun onSuccess(code: Int, msg: String?, obj: Any?) {
                    viewModel.deleteFawryOperations(transactionId.toInt())

                    showMessage(" تم استرجاع المبلغ اليكم $msg ")
                }

                override fun onFailed(code: Int, msg: String?) {
                    viewModel.deleteFawryOperations(transactionId.toInt())
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
        binding.servicesRv.adapter = null
    }


    @RequiresApi(Build.VERSION_CODES.HONEYCOMB)
    private fun getData() {
        getDataFromServer()

        checkCancelTransactions()
        addDynamicAdds()


        addFixedAds()
    }

    private fun addDynamicAdds() {

        //  viewModel.getImageAds(sharedHelper?.getUserToken().toString())

        lifecycleScope.launch {
            viewModel.dynamicAdsState.collect { result ->
                when (result) {
                    is NetworkResult.Error -> {

                    }

                    is NetworkResult.Loading -> binding.shimmerAdsContainer.startShimmerAnimation()

                    is NetworkResult.Success -> {
                        binding.shimmerAdsContainer.stopShimmerAnimation()
                        binding.shimmerAdsContainer.visibility = View.GONE
                        binding.adsViewPager.visibility = View.VISIBLE


                        imageAdsAdapter = ImageAdsAdapter(result.data?.data?.data!!)

                        Handler().postDelayed({
                            binding.adsViewPager.setCurrentItem(2, true)
                        }, 10)

                        binding.adsViewPager.offscreenPageLimit = 3
                        binding.adsViewPager.clipChildren = false
                        binding.adsViewPager.clipToPadding = false
                        binding.adsViewPager.getChildAt(0).overScrollMode =
                            RecyclerView.OVER_SCROLL_NEVER
                        val transformer = CompositePageTransformer()
                        transformer.addTransformer(MarginPageTransformer(40))
                        transformer.addTransformer { page, position ->
                            val r = 1 - abs(position)
                            page.scaleY = 0.85f + r * 0.14f
                        }

                        binding.adsViewPager.setPageTransformer(transformer)
                        binding.adsViewPager.adapter = imageAdsAdapter
                    }

                    null -> {
                        binding.shimmerAdsContainer.startShimmerAnimation()

                    }
                }
            }
        }
    }

    private fun addFixedAds() {
        val appAds =
            mutableListOf(R.drawable.forsa, R.drawable.adone, R.drawable.adtwo, R.drawable.adthree)

        val flipAdsAdapter = FlipperAdapter(requireActivity(), appAds, binding.esh7enlyAds)
        binding.esh7enlyAds.adapter = flipAdsAdapter
    }

    private fun getDataFromServer() {
        Log.d(TAG, "diaa getDataFromServer: ")

        binding.shimmerViewContainer.startShimmerAnimation()

        lifecycleScope.launch {

            viewModel.fetchData()

            viewModel.categoriesResponse.collect { response ->
                when (response) {
                    null -> {}
                    is NetworkResult.Error -> {
                        dialog.showErrorDialogWithAction(
                            response.message, resources.getString(R.string.app__ok)
                        ) {
                            dialog.cancel()

                        }.show()
                    }

                    is NetworkResult.Loading -> {
                        binding.shimmerViewContainer.startShimmerAnimation()

                    }

                    is NetworkResult.Success -> {
                        binding.shimmerViewContainer.stopShimmerAnimation()
                        binding.shimmerViewContainer.visibility = View.GONE
                        binding.servicesRv.visibility = View.VISIBLE

                        categories = response.data?.data

                        val other = CategoryData(
                            name = "خدمات أخرى",
                            id = 0
                        )

                        val filteredCategory = listOf(categories!![0], categories!![1], other)

                        replaceData(filteredCategory)
                    }
                }
            }


//           viewModel.getCategoriesNewFlow(sharedHelper?.getUserToken().toString())
//
//           viewModel._categoriesResponse.collect{
//               response->
//               when(response)
//               {
//                   is NetworkResult.Error -> {
//                       dialog.showErrorDialogWithAction(
//                           response.message, resources.getString(R.string.app__ok)
//                       ) {
//                           dialog.cancel()
//
//                       }.show()
//                   }
//                   is NetworkResult.Loading -> {
//                       binding.shimmerViewContainer.startShimmerAnimation()
//
//                   }
//                   is NetworkResult.Success -> {
//                       binding.shimmerViewContainer.stopShimmerAnimation()
//                       binding.shimmerViewContainer.visibility = View.GONE
//                       binding.servicesRv.visibility = View.VISIBLE
//
//                       categories = response.data?.data
//
//                       val other = CategoryData(
//                           name_ar = "خدمات أخرى",
//                           name_en = "Other Service",
//                           id = 0
//                       )
//
//                       val filteredCategory = listOf(categories!![0], categories!![1], other)
//
//                       replaceData(filteredCategory)
//
//                   }
//                   null -> {
//
//                   }
//               }
//           }
//       }


//            viewModel.getCategoriesNew(sharedHelper?.getUserToken().toString(),
//                object : OnResponseListener {
//                    override fun onSuccess(code: Int, msg: String?, obj: Any?) {
//                        categories = obj as List<CategoryData>
//
//                        val other = CategoryData(
//                            name_ar = "خدمات أخرى",
//                            name_en = "Other Service",
//                            id = 0
//                        )
//
//                        val filteredCategory = listOf(categories!![0], categories!![1], other)
//
//                        replaceData(filteredCategory)
//
//                        binding.shimmerViewContainer.stopShimmerAnimation()
//                        binding.shimmerViewContainer.visibility = View.GONE
//                        binding.servicesRv.visibility = View.VISIBLE
//                    }
//
//                    override fun onFailed(code: Int, msg: String?) {
//                        Log.d(TAG, "onFailed: ")
//
//                        dialog.showErrorDialogWithAction(
//                            msg, resources.getString(R.string.app__ok)
//                        ) {
//                            dialog.cancel()
//
//                            if (code == Constants.CODE_UNAUTH_NEW ||
//                                code.toString() == Constants.CODE_HTTP_UNAUTHORIZED
//                            ) {
//                                NavigateToActivity.navigateToAuthActivity(requireActivity())
//                            }
//                        }.show()
//                    }
//                })

        }
    }

    private fun replaceData(category: List<CategoryData>) {
        lifecycleScope.launch(Dispatchers.Main)
        {
            categoriesAdapter = CategoryAdapterNew(category, this@HomeFragment)
            binding.servicesRv.adapter = categoriesAdapter
        }
    }

    override fun click(category: CategoryData) {
        if (category.id == 0) {

            replaceData(categories!!)

        } else {
            NavigateToActivity.navigateToProviderActivity(requireActivity(), category)
        }
    }
}