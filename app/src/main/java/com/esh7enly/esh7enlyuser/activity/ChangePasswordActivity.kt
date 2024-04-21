package com.esh7enly.esh7enlyuser.activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.esh7enly.data.sharedhelper.SharedHelper
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.click.OnResponseListener
import com.esh7enly.esh7enlyuser.databinding.ActivityChangePasswordBinding
import com.esh7enly.esh7enlyuser.util.AppDialogMsg
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.IToolbarTitle
import com.esh7enly.esh7enlyuser.util.Language
import com.esh7enly.esh7enlyuser.util.NavigateToActivity
import com.esh7enly.esh7enlyuser.util.ProgressDialog
import com.esh7enly.esh7enlyuser.viewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChangePasswordActivity : AppCompatActivity(),IToolbarTitle
{
    private val ui by lazy{
        ActivityChangePasswordBinding.inflate(layoutInflater)
    }

    var sharedHelper: SharedHelper? = null
        @Inject set

    private val dialog by lazy {
        AppDialogMsg(this, false)
    }

    private val userViewModel: UserViewModel by viewModels()

    private val pDialog by lazy{
        ProgressDialog.createProgressDialog(this)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)

        Language.setLanguageNew(this, Constants.LANG)

        ui.fillOldPassword.hint = resources.getString(R.string.enter_old_password)
        ui.fillNewPassword.hint = resources.getString(R.string.enter_new_password)
        ui.userPasswordInstructions.text = resources.getString(R.string.password_instructions)
        ui.fillConfirmPassword.hint = resources.getString(R.string.confirm_new_password)
        ui.btnChangePassword.text = resources.getString(R.string.change_password)


//        pDialog.setMessage(Utils.getSpannableString(this,resources.getString(R.string.message__please_wait)))
//        pDialog.setCancelable(false)

        initToolBar()

        ui.btnChangePassword.setOnClickListener { changePassword() }
    }

    private fun changePassword()
    {
        val oldPassword = ui.oldPassword.text.toString().trim()
        val newPassword = ui.newPassword.text.toString().trim()
        val confirmNewPassword = ui.confirmNewPassword.text.toString().trim()

        if(TextUtils.isEmpty(oldPassword))
        {
            ui.oldPassword.error = resources.getString(R.string.type_old_password)
        }
        else if(TextUtils.isEmpty(newPassword))
        {
            ui.newPassword.error = resources.getString(R.string.type_new_password)
        }
        else if(TextUtils.isEmpty(confirmNewPassword))
        {
            ui.confirmNewPassword.error = resources.getString(R.string.type_confirm_new_password)
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
            pDialog.show()

            userViewModel.updatePassword(sharedHelper?.getUserToken().toString(),oldPassword,newPassword,
                object : OnResponseListener {
                    override fun onSuccess(code: Int, msg: String?, obj: Any?)
                    {
                        pDialog.cancel()
                        dialog.showErrorDialogWithAction(msg,resources.getString(R.string.app__ok))
                        {
                            dialog.cancel()
                            sharedHelper?.setUserToken("")
                            NavigateToActivity.navigateToMainActivity(this@ChangePasswordActivity)

                        }.show()
                    }

                    override fun onFailed(code: Int, msg: String?)
                    {
                        pDialog.cancel()
                        dialog.showErrorDialogWithAction(msg,resources.getString(R.string.app__ok))
                        {
                            dialog.cancel()

                            if (code.toString() == Constants.CODE_UNAUTH ||
                                code.toString() == Constants.CODE_HTTP_UNAUTHORIZED)
                            {
                                NavigateToActivity.navigateToMainActivity(this@ChangePasswordActivity)
                            }

                        }.show()
                    }
                })
        }
    }

    private fun isValidPassword(password: String): Boolean {
        if (password.length < 8) return false
        if (password.filter { it.isDigit() }.firstOrNull() == null) return false
        if (password.filter { it.isLetter() }.filter { it.isUpperCase() }.firstOrNull() == null) return false
        if (password.filter { it.isLetter() }.filter { it.isLowerCase() }.firstOrNull() == null) return false
        if (password.filter { !it.isLetterOrDigit() }.firstOrNull() == null) return false

        return true
    }

    override fun initToolBar()
    {
        ui.changePasswordToolbar.title = resources.getString(R.string.change_password)

        ui.changePasswordToolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }
}