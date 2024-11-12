package com.esh7enly.esh7enlyuser.activity

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.databinding.ActivityAuthBinding
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.Language
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private val ui by lazy {
        ActivityAuthBinding.inflate(layoutInflater)
    }

    private lateinit var navController: NavController
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        //installSplashScreen()
        Language.setLanguageNew(this, Constants.LANG)

        setContentView(ui.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.my_auth_nav) as NavHostFragment

        navController = navHostFragment.navController
        setStartDestination(navController)

    }

    private fun setStartDestination(navController: NavController) {
        val graph = navController.navInflater.inflate(R.navigation.auth_nav)

        // Set start destination based on the condition
        graph.setStartDestination(
            if (Constants.isSkip) {
                R.id.loginFragment
            } else {
                R.id.phoneFragment 
            }
        )
        navController.graph = graph
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
            setTheme(R.style.Theme_AuthActivity)
        }
    }

}