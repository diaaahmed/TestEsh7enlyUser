package com.esh7enly.esh7enlyuser.activity

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.view.View
import android.view.animation.AnticipateInterpolator

import androidx.annotation.RequiresApi
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.esh7enly.domain.ApiResponse
import com.esh7enly.domain.entity.loginresponse.LoginResponse
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.databinding.ActivityMainBinding
import com.esh7enly.esh7enlyuser.util.*
import com.google.firebase.messaging.FirebaseMessaging

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
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
        //initSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(ui.root)

       // validateToken()

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

//        ui.lifecycleOwner = this
//        ui.loginViewModel = userViewModel

        ui.phoneNumber.setText(sharedHelper?.getUserName())

//        pDialog.setMessage(
//            Utils.getSpannableString(
//                this,
//                resources.getString(R.string.message__please_wait)
//            )
//        )
//        pDialog.setCancelable(false)


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

    private fun initSplashScreen()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        {
            installSplashScreen()

            splashScreen.setOnExitAnimationListener {splashScreenView->
                val slideUp = ObjectAnimator.ofFloat(
                    splashScreenView, View.TRANSLATION_Y,0F,splashScreenView.height.toFloat()
                )

                slideUp.interpolator = AnticipateInterpolator()
                slideUp.duration = 1000L

                slideUp.doOnEnd { splashScreenView.remove() }
                slideUp.start()
            }
        }
        else
        {
            setTheme(R.style.Theme_Test)
        }


    }

    private fun validateToken()
    {
        userViewModel.token = sharedHelper?.getUserToken().toString()

        userViewModel.validateTokenResponseUser(userViewModel.token)

        lifecycleScope.launch {
            userViewModel.login.observe(this@MainActivity)
            {
                if (it == true)
                {
                    NavigateToActivity.navigateToHomeActivity(this@MainActivity)
                    return@observe
                }
                else
                {
                    setContentView(ui.root)
                    return@observe
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun login()
    {
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
            }
            else if(phoneNumber.length > 11)
            {
                alertDialog.showWarningDialog(
                    resources.getString(R.string.error_message__wrong_phone),
                    resources.getString(R.string.app__ok)
                )
                alertDialog.show()
            }
            else {
                pDialog.show()

               // pDialog.show()

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
        try{
            userViewModel.userLogin(phoneNumber, password, token).observe(this)
            { response ->
                if (response.isSuccessful)
                {
                  //  pDialog.cancel()
                    pDialog.cancel()

                    if (!response?.body!!.status!!)
                    {
                        showDialogWithAction(response.body!!.message!!)

                    } else {
                        successLoginNavigateToHome(response,password)

                    }
                } else {
                   // pDialog.cancel()
                    pDialog.cancel()
                    showDialogWithAction(response.errorMessage.toString())
                }
            }
        }
        catch (e: Exception)
        {
          //  pDialog.cancel()
            pDialog.cancel()
            showDialogWithAction(e.message.toString())
        }
    }

    private fun successLoginNavigateToHome(
        response: ApiResponse<LoginResponse>,
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