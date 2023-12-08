package com.esh7enly.esh7enlyuser.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.esh7enly.esh7enlyuser.BuildConfig
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.databinding.ActivityHomeBinding
import com.esh7enly.esh7enlyuser.util.Constants
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


    @RequiresApi(Build.VERSION_CODES.R)
    private fun requestPermissionHere()
    {
        val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")

        startActivity(
            Intent(
                Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                uri
            )
        )
    }

//    private fun requestPermissionHere() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) ==
//            PackageManager.PERMISSION_GRANTED
//        ) {
//            Toast.makeText(this, "Permission is granted", Toast.LENGTH_SHORT).show()
//        } else {
//            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES)) {
//                Toast.makeText(this, "Should show", Toast.LENGTH_SHORT).show()
//
//            } else {
//                val permissionA = arrayOf(
//                    Manifest.permission.READ_MEDIA_AUDIO,
//                    Manifest.permission.READ_MEDIA_VIDEO,
//                    Manifest.permission.READ_MEDIA_IMAGES
//
//                )
//
//                requestPermissions(permissionA, Constants.REQUEST_CODE__PERMISSION_CODE)
//                Toast.makeText(this, "Should else", Toast.LENGTH_SHORT).show()
//
//            }
//
//            //requestResult.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
//        }
//    }


    private val requestResult = registerForActivityResult<String, Boolean>(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean? ->

        if (isGranted == true) {
            Toast.makeText(this, "Allowed", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Not allowed", Toast.LENGTH_SHORT).show()

        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == Constants.REQUEST_CODE__PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}