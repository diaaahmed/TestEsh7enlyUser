package com.esh7enly.esh7enlyuser.util

import android.view.View
import androidx.core.content.ContextCompat
import com.esh7enly.esh7enlyuser.R
import com.google.android.material.snackbar.Snackbar

fun View.showSnackBarError(errorMessage:String)
{
    Snackbar.make(this,errorMessage,Snackbar.LENGTH_INDEFINITE)
        .setAction("Ok")
        {

        }
        .setActionTextColor(ContextCompat.getColor(this.context,R.color.white))
        .show()
}

fun View.showRetrySnackBarError(errorMessage:String,retry: () -> Unit)
{
    Snackbar.make(this,errorMessage,Snackbar.LENGTH_INDEFINITE)
        .setAction("Retry")
        {
            retry.invoke()
        }
        .setActionTextColor(ContextCompat.getColor(this.context,R.color.white))
        .show()
}