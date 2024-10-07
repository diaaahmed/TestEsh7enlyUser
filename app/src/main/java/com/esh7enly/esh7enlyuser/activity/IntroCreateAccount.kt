package com.esh7enly.esh7enlyuser.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.esh7enly.esh7enlyuser.databinding.ActivityIntroCreateAccountBinding
import com.esh7enly.esh7enlyuser.util.Constants
import com.esh7enly.esh7enlyuser.util.NavigateToActivity

class IntroCreateAccount : AppCompatActivity()
{
    private val ui by lazy {
        ActivityIntroCreateAccountBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)

        ui.btnSkip.setOnClickListener {
            NavigateToActivity.navigateToAuthActivity(this)
        }

        ui.btnNewAccount.setOnClickListener {
            Constants.isSkip = false
            NavigateToActivity.navigateToAuthActivity(this)
        }
    }
}