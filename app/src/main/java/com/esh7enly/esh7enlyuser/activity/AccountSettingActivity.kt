package com.esh7enly.esh7enlyuser.activity

import android.os.Build

import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.esh7enly.domain.entity.scedulelistresponse.Data
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.adapter.ScheduleListAdapter
import com.esh7enly.esh7enlyuser.click.OnResponseListener
import com.esh7enly.esh7enlyuser.databinding.ActivityAccountSettingBinding
import com.esh7enly.esh7enlyuser.util.ChooseLanguage
import com.esh7enly.esh7enlyuser.util.IToolbarTitle
import com.esh7enly.esh7enlyuser.util.NavigateToActivity
import com.esh7enly.esh7enlyuser.util.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AccountSettingActivity : BaseActivity(),IToolbarTitle
{
    private val ui by lazy{
        ActivityAccountSettingBinding.inflate(layoutInflater)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)

        pDialog.setMessage(Utils.getSpannableString(this,resources.getString(R.string.message__please_wait)))
        pDialog.setCancelable(false)

        initToolBar()

        ui.changeName.setOnClickListener { NavigateToActivity.navigateToChangeUserNameActivity(this) }

        ui.changePassword.setOnClickListener { NavigateToActivity.navigateToChangePasswordActivity(this) }

        ui.changeLanguage.setOnClickListener { showLanguage() }

        ui.myBills.setOnClickListener { checkScheduleVisibility() }

    }

    private fun checkScheduleVisibility() {
        if(ui.scheduleRv.visibility == View.GONE)
        {
            getScheduleList()
        }
        else
        {
            ui.scheduleRv.visibility = View.GONE
        }
    }

    private fun getScheduleList() {
        pDialog.show()

        lifecycleScope.launch {
            serviceViewModel.getScheduleList(sharedHelper?.getUserToken().toString(),
                object : OnResponseListener {
                    override fun onSuccess(code: Int, msg: String?, obj: Any?)
                    {
                        pDialog.cancel()
                        ui.scheduleRv.visibility = View.VISIBLE

                        val data = obj as List<Data>
                        val scheduleListAdapter = ScheduleListAdapter(data)
                        ui.scheduleRv.setHasFixedSize(true)
                        ui.scheduleRv.adapter = scheduleListAdapter

                    }

                    override fun onFailed(code: Int, msg: String?)
                    {
                    }
                })
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun showLanguage() {
        ChooseLanguage.showLanguage(this,sharedHelper!!)
    }

    override fun initToolBar() {
        ui.settingToolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

}