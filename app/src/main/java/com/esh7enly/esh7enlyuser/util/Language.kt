package com.esh7enly.esh7enlyuser.util

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.util.*

object Language
{
     @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
     fun setLanguageNew(context: Context, language:String?)
    {
        val locale = Locale(language)
        val dm = context.resources.displayMetrics
        val conf = context.resources.configuration
        conf.setLocale(locale)
        context.resources.updateConfiguration(conf, dm)

    }
}