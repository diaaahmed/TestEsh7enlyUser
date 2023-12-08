package com.esh7enly.esh7enlyuser.activity

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import androidx.activity.viewModels
import com.esh7enly.data.sharedhelper.SharedHelper
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.click.OnResponseListener
import com.esh7enly.esh7enlyuser.databinding.ActivityForgetPasswordBinding
import com.esh7enly.esh7enlyuser.util.AppDialogMsg
import com.esh7enly.esh7enlyuser.util.Connectivity
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.IToolbarTitle
import com.esh7enly.esh7enlyuser.util.NavigateToActivity
import com.esh7enly.esh7enlyuser.util.Utils
import com.esh7enly.esh7enlyuser.viewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ForgetPasswordActivity : AppCompatActivity(),IToolbarTitle
{
    private val ui by lazy {
        ActivityForgetPasswordBinding.inflate(layoutInflater)
    }

    var connectivity: Connectivity? = null
        @Inject set

    private val pDialog by lazy{
        ProgressDialog(this,R.style.MyAlertDialogStyle)
    }

    var sharedHelper: SharedHelper? = null
        @Inject set

    private val dialog by lazy {
        AppDialogMsg(this, false)
    }

    private val userViewModel: UserViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)

        pDialog.setMessage(Utils.getSpannableString(this,resources.getString(R.string.message__please_wait)))
        pDialog.setCancelable(false)

        initToolBar()

        ui.btnChangePassword.setOnClickListener {
            createNewPassword()
        }
    }

    private fun createNewPassword()
    {
        val newPassword = ui.newPassword.text.toString().trim()
        val confirmNewPassword = ui.confirmNewPassword.text.toString().trim()

        if(TextUtils.isEmpty(newPassword))
        {
            ui.newPassword.error = resources.getString(R.string.type_old_password)
        }
        else if(TextUtils.isEmpty(confirmNewPassword))
        {
            ui.confirmNewPassword.error = resources.getString(R.string.type_new_password)
        }

        else if(newPassword != confirmNewPassword)
        {
            ui.confirmNewPassword.error = resources.getString(R.string.type_confirm_new_password)
            ui.newPassword.error = resources.getString(R.string.type_new_password)

        }
        else if(!isValidPassword(newPassword))
        {
            ui.newPassword.error = "Not valid"
            ui.confirmNewPassword.error = "Not valid"
        }
        else
        {
            if(connectivity?.isConnected == true)
            {
                pDialog.show()

                sendRequestForgetPassword(newPassword, confirmNewPassword)
            }
            else
            {
                dialog.showWarningDialog(resources.getString(R.string.error_message__blank_phone_number),
                    resources.getString(R.string.app__ok))
                dialog.show()
            }

        }

    }

    private fun sendRequestForgetPassword(newPassword:String, confirmNewPassword:String)
    {
        val phone = intent.getStringExtra("phone")
        val otp = intent.getStringExtra("otp")

        userViewModel.createNewPassword(phone.toString(),newPassword,confirmNewPassword,otp.toString(),
            object : OnResponseListener {
                override fun onSuccess(code: Int, msg: String?, obj: Any?)
                {
                    successNewPassword(msg)
                }

                override fun onFailed(code: Int, msg: String?)
                {
                    failedNewPassword(msg,code)
                }
            })
    }

    private fun successNewPassword(msg:String?) {
        pDialog.cancel()
        dialog.showSuccessDialog(msg,resources.getString(R.string.app__ok))
        {
            dialog.cancel()
            sharedHelper?.setUserToken("")
            NavigateToActivity.navigateToMainActivity(this@ForgetPasswordActivity)

        }
        dialog.show()
    }

    private fun failedNewPassword(msg: String?,code:Int?) {
        pDialog.cancel()
        dialog.showErrorDialogWithAction(msg,resources.getString(R.string.app__ok))
        {
            dialog.cancel()

            if (code.toString() == Constants.CODE_UNAUTH ||
                code.toString() == Constants.CODE_HTTP_UNAUTHORIZED)
            {
                NavigateToActivity.navigateToMainActivity(this@ForgetPasswordActivity)
            }

        }.show()
    }


    private fun isValidPassword(password: String): Boolean
    {
        if (password.length < 8) return false
        if (password.filter { it.isDigit() }.firstOrNull() == null) return false
        if (password.filter { it.isLetter() }.filter { it.isUpperCase() }.firstOrNull() == null) return false
        if (password.filter { it.isLetter() }.filter { it.isLowerCase() }.firstOrNull() == null) return false
        if (password.filter { !it.isLetterOrDigit() }.firstOrNull() == null) return false

        return true
    }

    override fun initToolBar()
    {
        ui.newPasswordToolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }
}