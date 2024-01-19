package com.esh7enly.esh7enlyuser.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.esh7enly.domain.ApiResponse
import com.esh7enly.domain.entity.loginresponse.LoginResponse
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.databinding.ActivityMainBinding
import com.esh7enly.esh7enlyuser.util.*
import com.google.firebase.messaging.FirebaseMessaging

import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : BaseActivity()
{
    private val ui by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val alertDialog by lazy {
        AppDialogMsg(this, false)
    }

    var cryptoData: CryptoData? = null
        @Inject set

    private var spannableString: SpannableString? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)

        setUpRemember()

        ui.phoneNumber.setText(sharedHelper?.getUserName())

        pDialog.setMessage(
            Utils.getSpannableString(
                this,
                resources.getString(R.string.message__please_wait)
            )
        )
        pDialog.setCancelable(false)

        val string = resources.getString(R.string.no_account)

        spannableString = SpannableString(string)

        val clickableSpan = object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(this@MainActivity, R.color.colorPrimary)
            }

            override fun onClick(widget: View) {
                startActivity(Intent(this@MainActivity, PhoneActivity::class.java))
                finishAffinity()
            }
        }

        ui.forgetPassword.setOnClickListener {
            val forgetPasswordIntent = Intent(this, PhoneActivity::class.java)
            forgetPasswordIntent.putExtra(Constants.FORGET_PASSWORD, Constants.FORGET_PASSWORD)
            startActivity(forgetPasswordIntent)
        }

        try {
            spannableString?.setSpan(clickableSpan, 23, 29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        } catch (e: Exception) {
            spannableString?.setSpan(clickableSpan, 15, 23, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        ui.noAccount.text = spannableString
        ui.noAccount.movementMethod = LinkMovementMethod.getInstance()

        ui.language.setOnClickListener {
            showLanguage()
        }

        ui.btnLogin.setOnClickListener {
            login()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun login()
    {
        if (connectivity?.isConnected == true)
        {
            val phoneNumber = ui.phoneNumber.text.toString().trim()
            val password = ui.password.text.toString().trim()

            if (phoneNumber.isEmpty()) {
                alertDialog.showWarningDialog(
                    resources.getString(R.string.error_message__blank_phone),
                    resources.getString(R.string.app__ok)
                )
                alertDialog.show()
            } else if (password.isEmpty()) {

                alertDialog.showWarningDialog(
                    resources.getString(R.string.error_message__blank_password),
                    resources.getString(R.string.app__ok)
                )
                alertDialog.show()
            } else {
                pDialog.show()
                getUserTokenFromFirebase(phoneNumber,password)
            }
        } else {
            alertDialog.showErrorDialogWithAction(
                resources.getString(R.string.no_internet_error),
                resources.getString(R.string.app__ok)
            ) {
                alertDialog.cancel()
            }.show()
        }
    }

    private fun getUserTokenFromFirebase(phoneNumber: String, password: String)
    {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful)
            {
                val token = task.result

                userLogin(phoneNumber,password,token)
            }
        }
    }


    private fun userLogin(phoneNumber:String,password: String,token:String)
    {
        userViewModel.login(phoneNumber, password, token).observe(this)
        { response ->
            if (response.isSuccessful)
            {
                pDialog.cancel()

                if (!response?.body!!.status!!)
                {
                    showDialogWithAction(response.body!!.message!!)

                } else {
                    successLoginNavigateToHome(response,password)

                }
            } else {
                pDialog.cancel()
                showDialogWithAction(response.errorMessage.toString())
            }
        }
    }

    private fun successLoginNavigateToHome(response: ApiResponse<LoginResponse>,
                                           password:String)
    {
        sharedHelper?.setStoreName(response.body?.data!!.name)
        sharedHelper?.setUserToken(response.body?.data!!.token)
        val userEmail = response.body?.data!!.email
        sharedHelper?.setUserEmail(userEmail)
        sharedHelper?.isRememberUser(true)

        Constants.SERVICE_UPDATE_NUMBER =
            response.body?.service_update_num

        sharedHelper?.setUserName(response.body?.data!!.name)
        sharedHelper?.setUserPhone(response.body?.data!!.mobile)

        if (ui.checkBox.isChecked) {
            sharedHelper?.setRememberPassword(true)
            saveUserPassword(password)
        } else {
            sharedHelper?.setRememberPassword(false)
            removeUserPassword()
        }

        NavigateToActivity.navigateToHomeActivity(this@MainActivity)
    }


    @SuppressLint("HardwareIds")

    private fun showDialogWithAction(message: String) {
        alertDialog.showErrorDialogWithAction(
            message, resources.getString(R.string.app__ok)
        ) {
            alertDialog.cancel()
        }.show()
    }

    @RequiresApi(Build.VERSION_CODES.HONEYCOMB)
    private fun showLanguage() {
       // val lang = sharedPreferences.getString("app_lang", Constants.EN)
        val lang = sharedHelper?.getAppLanguage()

        val checkedItem = if (lang == "ar") {
            0
        } else {
            1
        }
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(resources.getString(R.string.chooseLanguage))
        dialog.setSingleChoiceItems(
            resources.getStringArray(R.array.data), checkedItem
        ) { dialogInterface, i ->
            when (i) {
                0 -> {
                    if (checkedItem == 0) {
                        return@setSingleChoiceItems
                    } else {
                        setLanguage("ar")
                    }
                }

                1 -> {
                    if (checkedItem == 1) {
                        return@setSingleChoiceItems
                    } else {
                        setLanguage("en")
                    }
                }
            }
            dialogInterface.dismiss()
        }

        val mDialog = dialog.create()
        mDialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.HONEYCOMB)
    private fun setLanguage(language: String?) {

        val locale = Locale(language)
        val dm = resources.displayMetrics
        // val conf = resources.configuration
        val conf = baseContext.resources.configuration
        conf.setLocale(locale)
        //       conf.locale = locale
        resources.updateConfiguration(conf, dm)
        Constants.LANG = language
        sharedHelper?.setAppLanguage(language!!)
        recreate()

    }

    override fun onDestroy() {
        super.onDestroy()
        spannableString = null
    }

    private fun setUpRemember() {
        ui.checkBox.isChecked = sharedHelper?.isRememberPassword() == true

        if (sharedHelper?.isRememberPassword() == true) {
            ui.password.setText(getSavePassword())
        }
    }

    private fun saveUserPassword(password: String) {
        sharedHelper?.setUserPassword(password)
    }

    private fun removeUserPassword() {
        sharedHelper?.setUserPassword("")
    }

    private fun getSavePassword(): String? = sharedHelper?.getUserPassword()

}