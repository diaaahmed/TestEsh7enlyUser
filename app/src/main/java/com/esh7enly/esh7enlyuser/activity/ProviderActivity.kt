package com.esh7enly.esh7enlyuser.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.esh7enly.domain.entity.providersNew.ProviderData
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.adapter.ProviderAdapter
import com.esh7enly.esh7enlyuser.click.OnResponseListener
import com.esh7enly.esh7enlyuser.click.ProviderClick
import com.esh7enly.esh7enlyuser.databinding.ActivityProviderBinding
import com.esh7enly.esh7enlyuser.util.AppDialogMsg
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.IToolbarTitle
import com.esh7enly.esh7enlyuser.util.Language
import com.esh7enly.esh7enlyuser.util.NavigateToActivity
import com.google.android.play.core.review.ReviewManagerFactory
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProviderActivity : BaseActivity(),ProviderClick,IToolbarTitle
{
    private val ui by lazy{
        ActivityProviderBinding.inflate(layoutInflater)
    }

    private val providerAdapter by lazy {
        ProviderAdapter(this)
    }

    private val dialog by lazy {
        AppDialogMsg(this, false)
    }

    private var categoryID = 0

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)

        Language.setLanguageNew(this, Constants.LANG)

        initToolBar()

        ui.providerRv.setHasFixedSize(true)

        categoryID = intent.getIntExtra(Constants.CATEGORY_ID,0)

        getData()
    }

    private fun checkApiReview()
    {
        val reviewManager = ReviewManagerFactory.create(this)
        reviewManager.requestReviewFlow().addOnCompleteListener {
            if(it.isSuccessful)
            {
                reviewManager.launchReviewFlow(this,it.result)
            }
        }
    }

    override fun initToolBar() {
        ui.providerToolbar.title = intent.getStringExtra(Constants.CATEGORY_NAME)?: ""

        ui.providerToolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed()}
    }

    private fun getData() {
        pDialog.show()
        serviceViewModel.getProvidersNew(sharedHelper?.getUserToken().toString(),categoryID.toString(),
            object : OnResponseListener {
                override fun onSuccess(code: Int, msg: String?, obj: Any?)
                {
                    pDialog.cancel()

                    val providers = obj as List<ProviderData>

                    providerAdapter.submitList(providers)

                    ui.providerRv.adapter = providerAdapter

                }

                override fun onFailed(code: Int, msg: String?)
                {
                    pDialog.cancel()

                    dialog.showErrorDialogWithAction(
                        msg, resources.getString(R.string.app__ok)
                    ) {
                        dialog.cancel()

                        if (code == Constants.CODE_UNAUTH_NEW ||
                            code.toString() == Constants.CODE_HTTP_UNAUTHORIZED
                        ) {
                            NavigateToActivity.navigateToAuthActivity(this@ProviderActivity)
                        }
                    }.show()
                }
            })
    }

    override fun click(provider: ProviderData)
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