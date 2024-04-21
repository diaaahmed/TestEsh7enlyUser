package com.esh7enly.esh7enlyuser.util

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.esh7enly.data.sharedhelper.SharedHelper
import com.esh7enly.esh7enlyuser.R
import com.esh7enly.esh7enlyuser.activity.SplachActivity
import java.util.*

object ChooseLanguage
{
     @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
     fun showLanguage(context:Activity,sharedHelper: SharedHelper)
    {
        val lang = sharedHelper.getAppLanguage()

        val checkedItem = if(lang == Constants.AR) {
            0
        } else {
            1
        }

        val dialog = AlertDialog.Builder(context)
        dialog.setTitle(context.resources.getString(R.string.chooseLanguage))
        dialog.setSingleChoiceItems(
            context.resources.getStringArray(R.array.data), checkedItem
        ) { dialogInterface, i ->
            when (i) {
                0 -> {
                    if(checkedItem == 0)
                    {
                        dialogInterface.dismiss()
                        return@setSingleChoiceItems
                    }
                    else
                    {
                        setLanguage("ar",context,sharedHelper)
                    }
                }
                1 -> {
                    if(checkedItem == 1)
                    {
                        dialogInterface.dismiss()
                        return@setSingleChoiceItems
                    }
                    else
                    {
                        setLanguage("en",context,sharedHelper)
                    }
                }
            }
            dialogInterface.dismiss()
        }

        val mDialog = dialog.create()
        mDialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
     fun setLanguage(language: String?, context: Activity,sharedHelper: SharedHelper)
    {
      //   LocaleHelper.setLocale(context,language)
        val locale = Locale(language)
        val dm = context.resources.displayMetrics
        val conf = context.resources.configuration
        conf.setLocale(locale)
        //       conf.locale = locale
        context.resources.updateConfiguration(conf, dm)
        Constants.LANG = language

        saveLanguageState(language,sharedHelper)

        context.startActivity(Intent(context,SplachActivity::class.java))
        context.finish()
    }

    private fun saveLanguageState(language: String?,sharedHelper: SharedHelper)
    {
        sharedHelper.setAppLanguage(language!!)
    }
}