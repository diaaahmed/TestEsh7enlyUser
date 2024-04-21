package com.esh7enly.esh7enlyuser.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.SpannableString

import android.util.Patterns
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.esh7enly.data.sharedhelper.SharedHelper
import com.esh7enly.domain.entity.RegisterModel
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.click.OnResponseListener
import com.esh7enly.esh7enlyuser.databinding.ActivitySignupBinding
import com.esh7enly.esh7enlyuser.util.AppDialogMsg
import com.esh7enly.esh7enlyuser.util.Connectivity
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.Language
import com.esh7enly.esh7enlyuser.util.ProgressDialog
import com.esh7enly.esh7enlyuser.viewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SignupActivity : AppCompatActivity()
{
    private val ui by lazy{
        ActivitySignupBinding.inflate(layoutInflater)
    }

    private val userViewModel: UserViewModel by viewModels()


    private val alertDialog by lazy {
        AppDialogMsg(this,false)
    }

    private val pDialog by lazy{
        ProgressDialog.createProgressDialog(this)
    }

    private var spannableString:SpannableString?= null

    var connectivity: Connectivity? = null
        @Inject set

    var sharedHelper: SharedHelper? = null
        @Inject set


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)

        Language.setLanguageNew(this, Constants.LANG)

        ui.txtRegister.text = resources.getString(R.string.register)
        ui.haveAccount.text = resources.getString(R.string.have_account)
        ui.btnSignup.text = resources.getString(R.string.signUp)
        ui.checkBox.text = resources.getString(R.string.accept_privacy)
        ui.fillFirstName.hint = resources.getString(R.string.first_name)
        ui.fillLastName.hint = resources.getString(R.string.last_name)
        ui.fillPassword.hint = resources.getString(R.string.your_password)
        ui.fillConfirmPassword.hint = resources.getString(R.string.confirm_password)
        ui.fillEmail.hint = resources.getString(R.string.email)

        getIntentData()


        ui.haveAccount.setOnClickListener {
            startActivity(Intent(this@SignupActivity,MainActivity::class.java))
            finish()
        }


        ui.btnSignup.setOnClickListener {
            pDialog.show()
            createAccount()
        }
    }

    private fun getIntentData() {
        userViewModel.phoneNumber = intent.getStringExtra(Constants.USER_PHONE)
    }

    private fun createAccount()
    {
        val firstName = ui.userFirstName.text.toString().trim()
        val lastName = ui.userLastName.text.toString().trim()
        val password = ui.userPassword.text.toString().trim()
        val confirmPassword = ui.confirmPassword.text.toString().trim()
        val email = ui.userEmail.text.toString()

        if(connectivity?.isConnected == true)
        {
            pDialog.cancel()

            if(firstName.isEmpty())
            {
                alertDialog.showWarningDialog(resources.getString(R.string.error_message__blank_first_name),
                    resources.getString(R.string.app__ok))
                alertDialog.show()
            }
            else if(lastName.isEmpty())
            {
                alertDialog.showWarningDialog(resources.getString(R.string.error_message__blank_last_name),
                    resources.getString(R.string.app__ok))
                alertDialog.show()
            }
            else if(password.isEmpty() || confirmPassword.isEmpty())
            {
                alertDialog.showWarningDialog(resources.getString(R.string.error_message__blank_password),
                    resources.getString(R.string.app__ok))
                alertDialog.show()
            }
            else if(password != confirmPassword)
            {
                alertDialog.showWarningDialog(resources.getString(R.string.password_not_match_confirm_password),
                    resources.getString(R.string.app__ok))
                alertDialog.show()
            }
            else if(!ui.checkBox.isChecked)
            {
                ui.checkBox.error = getString(R.string.required)
            } else if(!isValidPassword(password))
            {
                alertDialog.showWarningDialog(getString(R.string.password_is_not_valid_please_use_aa1),
                    resources.getString(R.string.app__ok))
                alertDialog.show()
            }
            else if(!isValidEmailId(email))
            {
                alertDialog.showWarningDialog(getString(R.string.email_is_not_valid),
                    resources.getString(R.string.app__ok))
                alertDialog.show()
            }
            else
            {
                val fullName = "$firstName $lastName"
                // create account with api
                userViewModel.registerNewAccount(RegisterModel(fullName,userViewModel.phoneNumber!!,password,email),
                    object : OnResponseListener {
                        override fun onSuccess(code: Int, msg: String?, obj: Any?)
                        {
                            alertDialog.showSuccessDialog(msg, resources.getString(R.string.app__ok))
                            {
                                startActivity(Intent(this@SignupActivity,MainActivity::class.java))
                                finish()
                            }
                            alertDialog.show()
                        }

                        override fun onFailed(code: Int, msg: String?)
                        {
                            alertDialog.showWarningDialog(msg,
                                resources.getString(R.string.app__ok))
                            alertDialog.show()
                        }
                    })
            }
        }
        else
        {
            pDialog.cancel()
            alertDialog.showWarningDialog(resources.getString(R.string.no_internet_error),
            resources.getString(R.string.app__ok))
            alertDialog.show()
        }


    }

    private fun isValidEmailId(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()


    override fun onDestroy() {
        super.onDestroy()
        spannableString = null
    }

    private fun isValidPassword(password: String): Boolean {
        if (password.length < 8) return false
        if (password.filter { it.isDigit() }.firstOrNull() == null) return false
        if (password.filter { it.isLetter() }.filter { it.isUpperCase() }.firstOrNull() == null) return false
        if (password.filter { it.isLetter() }.filter { it.isLowerCase() }.firstOrNull() == null) return false
        if (password.filter { !it.isLetterOrDigit() }.firstOrNull() == null) return false

        return true
    }

}