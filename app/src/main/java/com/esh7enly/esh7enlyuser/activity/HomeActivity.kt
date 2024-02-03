package com.esh7enly.esh7enlyuser.activity

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.databinding.ActivityHomeBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private val ui by lazy {
        ActivityHomeBinding.inflate(layoutInflater)
    }
    lateinit var navController: NavController

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        setContentView(ui.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.my_nav) as NavHostFragment

        navController = navHostFragment.navController

        // For connect between bottom navigation and navgraph
        NavigationUI.setupWithNavController(ui.bottomNav, navController)

    }

}