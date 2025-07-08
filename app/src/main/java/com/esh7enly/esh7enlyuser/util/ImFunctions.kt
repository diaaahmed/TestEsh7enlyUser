package com.esh7enly.esh7enlyuser.util

import android.app.Activity
import android.util.Patterns
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.esh7enly.esh7enlyuser.BuildConfig
import com.esh7enly.esh7enlyuser.R

fun sendIssueToCrashlytics(
    msg: String,
    functionName: String,
    key :String = CrashlyticsUtils.LOGIN_KEY,
    provider :String = CrashlyticsUtils.LOGIN_PROVIDER,) {
    CrashlyticsUtils.sendCustomLogToCrashlytics<CrashMessage>(
        msg,
        key to msg,
        provider to functionName,
    )
}

fun isValidPassword(password: String): Boolean {
    if (password.length < 8) return false
    if (password.firstOrNull { it.isDigit() } == null) return false
    if (password.filter { it.isLetter() }.firstOrNull { it.isUpperCase() } == null) return false
    if (password.filter { it.isLetter() }.firstOrNull { it.isLowerCase() } == null) return false
    if (password.firstOrNull { !it.isLetterOrDigit() } == null) return false

    return true
}

fun isValidEmailId(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()


fun showErrorDialogWithAction(
    activity: Activity, dialog:AppDialogMsg,
    msg:String, okTitle:String,code:Int)
{
    dialog.showErrorDialogWithAction(msg,okTitle)
    {
        dialog.cancel()

        if (code == Constants.CODE_UNAUTHENTIC_NEW ||
            code.toString() == Constants.CODE_HTTP_UNAUTHORIZED)
        {
            NavigateToActivity.navigateToAuthActivity(activity)
        }

    }.show()
}

@BindingAdapter("providerImage")
fun providerImage(imageView: ImageView, providerImageUrl:String?= "")
{
    Glide.with(imageView.context).load(BuildConfig.IMAGE_URL + providerImageUrl) //.crossFade()
        .placeholder(R.drawable.new_logo_trans)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .apply(
            RequestOptions() //.onlyRetrieveFromCache(!isWifi)
                .dontAnimate()
                .centerInside()
        )
        .into(imageView)

}