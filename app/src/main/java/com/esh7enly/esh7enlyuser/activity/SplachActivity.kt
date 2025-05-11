package com.esh7enly.esh7enlyuser.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle

import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.databinding.ActivitySplachBinding
import com.esh7enly.esh7enlyuser.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplachActivity : BaseActivity()
{
    private val ui by lazy { ActivitySplachBinding.inflate(layoutInflater) }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        setContentView(ui.root)

        val appLanguage = sharedHelper?.getAppLanguage()

        Constants.LANG = appLanguage

        Language.setLanguageNew(this, appLanguage)

        ui.esh7enlySlogan.text = resources.getString(R.string.esh7enly_solgan)
        ui.fastText.text = resources.getString(R.string.fastest)

        lifecycleScope.launch {

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

    private fun validateToken()
    {
        userViewModel.validateTokenResponseUser()

        lifecycleScope.launch {
            userViewModel.loginState.observe(this@SplachActivity)
            {
                if (it == true) {
                    NavigateToActivity.navigateToHomeActivity(this@SplachActivity)
                    return@observe
                } else {
                    NavigateToActivity.navigateToAuthActivity(this@SplachActivity)
                }
            }
        }
    }
}