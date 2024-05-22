package com.esh7enly.esh7enlyuser.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.SpannableString


import androidx.annotation.RequiresApi
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
class MainActivity : BaseActivity() {

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

        ui.lifecycleOwner = this
        ui.loginViewModel = userViewModel

        val appLanguage = sharedHelper?.getAppLanguage()

        Constants.LANG = appLanguage

        Language.setLanguageNew(this, Constants.LANG)

        ui.textView.text = resources.getString(R.string.welcome_back)
        ui.checkBox.text = resources.getString(R.string.remember_me)
        ui.forgetPassword.text = resources.getString(R.string.forget_password)
        ui.btnLogin.text = resources.getString(R.string.login_btn)
        ui.language.text = resources.getString(R.string.language)
        ui.noAccount.text = resources.getString(R.string.no_account)
        ui.fillPhoneNumber.hint = resources.getString(R.string.enter_phone_number)
        ui.fillPassword.hint = resources.getString(R.string.enter_password)

        setUpRemember()

        ui.forgetPassword.setOnClickListener {
            val forgetPasswordIntent = Intent(this, PhoneActivity::class.java)
            forgetPasswordIntent.putExtra(Constants.FORGET_PASSWORD, Constants.FORGET_PASSWORD)
            startActivity(forgetPasswordIntent)
        }

        ui.language.setOnClickListener {
            showLanguage()
        }

        ui.noAccount.setOnClickListener {
            startActivity(Intent(this@MainActivity, PhoneActivity::class.java))
            finishAffinity()
        }

        ui.btnLogin.setOnClickListener {
            login()
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun login() {

        if (connectivity?.isConnected == true) {
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
            } else if (phoneNumber.length > 11) {
                alertDialog.showWarningDialog(
                    resources.getString(R.string.error_message__wrong_phone),
                    resources.getString(R.string.app__ok)
                )
                alertDialog.show()
            } else {
                pDialog.show()

                getUserTokenFromFirebase()
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

    private fun getUserTokenFromFirebase() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                userLogin(token)
            }
        }
    }


    private fun userLogin(token: String) {
        try {
            userViewModel.userLogin(token).observe(this)
            { response ->
                if (response.isSuccessful)
                {
                    pDialog.cancel()

                    if (!response?.body!!.status!!)
                    {
                        showDialogWithAction(response.body!!.message!!)
                        logAuthIssueToCrashlytics(response.body!!.message!!)
                    }
                    else {
                        successLoginNavigateToHome(response)
                    }
                } else {
                    pDialog.cancel()
                    showDialogWithAction(response.errorMessage.toString())
                    logAuthIssueToCrashlytics(response.errorMessage.toString())

                }
            }
        } catch (e: Exception) {
            pDialog.cancel()
            logAuthIssueToCrashlytics(e.message.toString())
            showDialogWithAction(e.message.toString())
        }
    }

    private fun successLoginNavigateToHome(
        response: ApiResponse<LoginResponse>
    ) {
        sharedHelper?.setStoreName(response.body?.data!!.name)
        sharedHelper?.setUserToken(response.body?.data!!.token)
        sharedHelper?.setUserEmail(response.body?.data!!.email)
        sharedHelper?.isRememberUser(true)

        Constants.SERVICE_UPDATE_NUMBER =
            response.body?.service_update_num

        sharedHelper?.setUserName(response.body?.data!!.name)
        sharedHelper?.setUserPhone(response.body?.data!!.mobile)

        if (ui.checkBox.isChecked) {
            sharedHelper?.setRememberPassword(true)
            saveUserPassword()
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
        val locale = Locale(language.toString())
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

    private fun saveUserPassword() {
        userViewModel.saveUserPassword()
    }

    private fun removeUserPassword() {
        userViewModel.removeUserPassword()
    }

    private fun getSavePassword(): String? = sharedHelper?.getUserPassword()

    private fun logAuthIssueToCrashlytics(msg: String) {
        CrashlyticsUtils.sendCustomLogToCrashlytics<LoginException>(
            msg,
            CrashlyticsUtils.LOGIN_KEY to msg,
            CrashlyticsUtils.LOGIN_PROVIDER to "Login with Phone and password",
        )
    }
}