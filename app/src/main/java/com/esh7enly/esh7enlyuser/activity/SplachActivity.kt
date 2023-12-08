package com.esh7enly.esh7enlyuser.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle

import androidx.annotation.RequiresApi
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplachActivity : BaseActivity() {

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // installSplashScreen()
        setContentView(R.layout.activity_splach)

        val appLanguage = sharedHelper?.getAppLanguage()

        Constants.LANG = appLanguage

        Language.setLanguageNew(this, appLanguage)

        lifecycleScope.launch {

            delay(3000)

            val isFirstStart = sharedHelper?.isFirstOpen()

            if (isFirstStart == true) {
                sharedHelper?.setFirstOpen(false)
                startActivity(Intent(this@SplachActivity, IntroActivity::class.java))
                finish()
            } else {
                validateToken()
            }

        }
    }

    private fun validateToken() {
        userViewModel.token = sharedHelper?.getUserToken().toString()

        serviceViewModel.validateTokenResponseUser(userViewModel.token)

        lifecycleScope.launch {
            serviceViewModel.login.observe(this@SplachActivity)
            {
                if (it == true) {
                    NavigateToActivity.navigateToHomeActivity(this@SplachActivity)
                    return@observe
                } else {
                    NavigateToActivity.navigateToMainActivity(this@SplachActivity)

                }
            }
        }
    }
}