package com.esh7enly.esh7enlyuser.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.esh7enly.data.sharedhelper.SharedHelper
import com.esh7enly.domain.entity.RegisterModel
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.click.OnResponseListener
import com.esh7enly.esh7enlyuser.databinding.ActivitySignupBinding
import com.esh7enly.esh7enlyuser.util.AppDialogMsg
import com.esh7enly.esh7enlyuser.util.Connectivity
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.Utils
import com.esh7enly.esh7enlyuser.viewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern
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
        ProgressDialog(this,R.style.MyAlertDialogStyle)
    }

    private var spannableString:SpannableString?= null

    var connectivity: Connectivity? = null
        @Inject set


    var sharedHelper: SharedHelper? = null
        @Inject set


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)

        getIntentData()

        pDialog.setMessage(Utils.getSpannableString(this,resources.getString(R.string.message__please_wait)))
        pDialog.setCancelable(false)

        val string = resources.getString(R.string.have_account)
        spannableString = SpannableString(string)

        val clickableSpan = object : ClickableSpan()
        {
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
            //    ds.color = resources.getColor(R.color.colorPrimary)
                ds.color = ContextCompat.getColor(this@SignupActivity,R.color.colorPrimary)
            }

            override fun onClick(widget: View)
            {
                startActivity(Intent(this@SignupActivity,MainActivity::class.java))
                finish()
            }
        }

        try
        {
            spannableString?.setSpan(clickableSpan,17,24, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        catch(e:Exception)
        {
            spannableString?.setSpan(clickableSpan,11,21, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        ui.noAccount.text = spannableString
        ui.noAccount.movementMethod = LinkMovementMethod.getInstance()


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
        val fullName = ui.userName.text.toString().trim()
        val password = ui.userPassword.text.toString().trim()
        val confirmPassword = ui.confirmPassword.text.toString().trim()
        val email = ui.userEmail.text.toString()

        if(connectivity?.isConnected == true)
        {
            pDialog.cancel()

            if(fullName.isEmpty())
            {
                alertDialog.showWarningDialog(resources.getString(R.string.error_message__blank_full_name),
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
                ui.checkBox.error = "Required"
            } else if(!isValidPassword(password))
            {
                alertDialog.showWarningDialog("Password is not valid please use (Aa1@#$%)",
                    resources.getString(R.string.app__ok))
                alertDialog.show()
            }
            else if(!isValidEmailId(email))
            {
                alertDialog.showWarningDialog("Email is not valid",
                    resources.getString(R.string.app__ok))
                alertDialog.show()
            }
            else
            {
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

    private fun isValidEmailId(email: String): Boolean {
        return Pattern.compile(
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        ).matcher(email).matches()
    }

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