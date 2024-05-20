package com.esh7enly.esh7enlyuser.activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.esh7enly.data.sharedhelper.SharedHelper
import com.esh7enly.domain.NetworkResult
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.databinding.ActivityChangeUserNameBinding
import com.esh7enly.esh7enlyuser.util.AppDialogMsg
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.IToolbarTitle
import com.esh7enly.esh7enlyuser.util.Language
import com.esh7enly.esh7enlyuser.util.NavigateToActivity
import com.esh7enly.esh7enlyuser.util.ProgressDialog
import com.esh7enly.esh7enlyuser.viewModel.UpdateProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChangeUserNameActivity : AppCompatActivity(), IToolbarTitle {

    private val ui by lazy {
        ActivityChangeUserNameBinding.inflate(layoutInflater)
    }

    var sharedHelper: SharedHelper? = null
        @Inject set

    private val dialog by lazy {
        AppDialogMsg(this, false)
    }

    private val updateProfileViewModel: UpdateProfileViewModel by viewModels()

    private val pDialog by lazy {
        ProgressDialog.createProgressDialog(this)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)

        ui.lifecycleOwner = this
        ui.updateProfileViewModel = updateProfileViewModel

        Language.setLanguageNew(this, Constants.LANG)

        ui.updateProfileToolbar.title = resources.getString(R.string.change_user_data)
        ui.btnUpdateData.text = resources.getString(R.string.update_data)
        ui.fillLastName.hint = resources.getString(R.string.last_name)
        ui.fillFirstName.hint = resources.getString(R.string.first_name)
        ui.fillNewEmail.hint = resources.getString(R.string.enter_new_email)
        ui.fillNewMobile.hint = resources.getString(R.string.enter_new_mobile)

        initToolBar()

       lifecycleScope.launch {
           updateProfileViewModel.updateProfileState.collect{
               result ->
               when(result)
               {
                   is NetworkResult.Error -> {
                       showErrorDialog(result.message,result.code)
                   }
                   is NetworkResult.Loading -> {
                       pDialog.show()
                   }
                   is NetworkResult.Success -> {
                       showSuccessDialog(result.data)
                   }
                   null -> {

                   }
               }
           }
       }
    }

    private fun showSuccessDialog(msg: String?) {
        pDialog.cancel()

        dialog.showSuccessDialog(msg, resources.getString(R.string.app__ok))
        {
            dialog.cancel()
            sharedHelper?.setUserToken("")
            NavigateToActivity.navigateToMainActivity(this@ChangeUserNameActivity)

        }
        dialog.show()
    }

    private fun showErrorDialog(msg: String?, code: Int?) {
        pDialog.cancel()

        dialog.showErrorDialogWithAction(msg, resources.getString(R.string.app__ok))
        {
            dialog.cancel()

            if (code.toString() == Constants.CODE_UNAUTH ||
                code.toString() == Constants.CODE_HTTP_UNAUTHORIZED
            ) {
                NavigateToActivity.navigateToMainActivity(this@ChangeUserNameActivity)
            }

        }.show()
    }

    override fun initToolBar() {
        ui.updateProfileToolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }
}