package com.esh7enly.esh7enlyuser.activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.esh7enly.data.sharedhelper.SharedHelper
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.click.OnResponseListener
import com.esh7enly.esh7enlyuser.databinding.ActivityChangeUserNameBinding
import com.esh7enly.esh7enlyuser.util.AppDialogMsg
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.IToolbarTitle
import com.esh7enly.esh7enlyuser.util.Language
import com.esh7enly.esh7enlyuser.util.NavigateToActivity
import com.esh7enly.esh7enlyuser.util.ProgressDialog
import com.esh7enly.esh7enlyuser.util.Utils
import com.esh7enly.esh7enlyuser.viewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChangeUserNameActivity : AppCompatActivity(), IToolbarTitle
{

    private val ui by lazy {
        ActivityChangeUserNameBinding.inflate(layoutInflater)
    }

    var sharedHelper: SharedHelper? = null
        @Inject set

    private val dialog by lazy {
        AppDialogMsg(this, false)
    }

    private val userViewModel: UserViewModel by viewModels()

    private val pDialog by lazy {
        ProgressDialog.createProgressDialog(this)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)

        Language.setLanguageNew(this, Constants.LANG)

        ui.updateProfileToolbar.title = resources.getString(R.string.change_user_data)
        ui.btnUpdateData.text = resources.getString(R.string.update_data)
        ui.fillLastName.hint = resources.getString(R.string.last_name)
        ui.fillFirstName.hint = resources.getString(R.string.first_name)
        ui.fillNewEmail.hint = resources.getString(R.string.enter_new_email)
        ui.fillNewMobile.hint = resources.getString(R.string.enter_new_mobile)

        initToolBar()

//        pDialog.setMessage(
//            Utils.getSpannableString(
//                this,
//                resources.getString(R.string.message__please_wait)
//            )
//        )
//        pDialog.setCancelable(false)

        ui.btnUpdateData.setOnClickListener { changeData() }

    }

    private fun changeData()
    {
        val newMobile = ui.newMobile.text.toString()
        val firstName = ui.userFirstName.text.toString()
        val lastName = ui.userLastName.text.toString()
        val newEmail = ui.newEmail.text.toString()

        val fullName = "$firstName $lastName"

        if (newMobile.isBlank() && firstName.isBlank() && lastName.isBlank() &&newEmail.isBlank())
        {
           return

        } else if (newEmail.isNotEmpty()) {
            if (!isValidEmailId(newEmail)) {
                showDialogInvalidEmail()
            } else {
                sendUpdateData(fullName, newMobile, newEmail)
            }
        } else {
            sendUpdateData(fullName, newMobile, newEmail)
        }
    }

    private fun showDialogInvalidEmail() {
        dialog.showWarningDialog(
            resources.getString(R.string.email_not_valid),
            resources.getString(R.string.app__ok)
        )
        dialog.show()
    }

    private fun sendUpdateData(newName: String, newMobile: String, newEmail: String) {
        pDialog.show()

        userViewModel.updateProfile(sharedHelper?.getUserToken().toString(),
            newName, newMobile, newEmail, object : OnResponseListener {
                override fun onSuccess(code: Int, msg: String?, obj: Any?) {
                    showSuccessDialog(msg)
                }

                override fun onFailed(code: Int, msg: String?) {

                    showErrorDialog(msg, code)
                }
            })
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

    private fun isValidEmailId(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()


    override fun initToolBar() {
        ui.updateProfileToolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }
}