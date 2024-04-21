package com.esh7enly.esh7enlyuser.activity

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator

import androidx.annotation.RequiresApi
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.databinding.ActivitySplachBinding
import com.esh7enly.esh7enlyuser.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplachActivity : BaseActivity() {

    private val ui by lazy {
        ActivitySplachBinding.inflate(layoutInflater)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?)
    {
      //  initSplashScreen()
        super.onCreate(savedInstanceState)

        setContentView(ui.root)

        val appLanguage = sharedHelper?.getAppLanguage()

        Constants.LANG = appLanguage

        Language.setLanguageNew(this, appLanguage)

        ui.esh7enlySlogan.text = resources.getString(R.string.esh7enly_solgan)
        ui.fastText.text = resources.getString(R.string.fastest)

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

    private fun initSplashScreen() {
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

    private fun validateToken() {
        userViewModel.token = sharedHelper?.getUserToken().toString()

        userViewModel.validateTokenResponseUser(userViewModel.token)

        lifecycleScope.launch {
            userViewModel.login.observe(this@SplachActivity)
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