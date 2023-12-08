package com.esh7enly.esh7enlyuser.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.esh7enly.esh7enlyuser.R

class PaytabsPolicy : AppCompatActivity()
{

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paytabs_policy)

        val privacy_txt = findViewById<TextView>(R.id.privacy_txt)

        val data = intent.getStringExtra("terms")

        if(data.equals("refund"))
        {
            privacy_txt.text = resources.getString(R.string.refund_policy)
        }
        else if(data.equals("terms"))
        {
            privacy_txt.text = resources.getString(R.string.terms_policy)

        }
        else
        {
            privacy_txt.text = resources.getString(R.string.general_policy)

        }
    }
}