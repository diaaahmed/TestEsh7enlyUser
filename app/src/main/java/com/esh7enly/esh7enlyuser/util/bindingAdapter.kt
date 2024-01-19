package com.esh7enly.esh7enlyuser.util

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.esh7enly.esh7enlyuser.R


@BindingAdapter(value = ["showFillPhoneNumber"])
fun showFillPhoneNumber(view:View,isShow:Boolean) {
    if(isShow)
    {
        view.visibility = View.VISIBLE
    }
    else
    {
        view.visibility = View.GONE

    }
}

@BindingAdapter(value = ["showFees"])
fun showFees(view:View,isShow:Boolean) {
    if(!isShow)
    {
        view.visibility = View.VISIBLE
    }
    else
    {
        view.visibility = View.GONE

    }
}

@BindingAdapter(value = ["bankWayColor"])
fun bankWayColor(textView: TextView,isShow:Boolean) {
    if(!isShow)
    {
        textView.setBackgroundColor(
            ContextCompat.getColor(
                textView.context,
                R.color.colorPrimary
            )
        )
        textView.setTextColor(ContextCompat.getColor(textView.context, R.color.white))
    }
    else
    {
        textView.setTextColor(ContextCompat.getColor(textView.context,R.color.grey_100_))
        textView.setBackgroundColor(
            ContextCompat.getColor(
                textView.context,
                R.color.white
            )
        )

    }
}

@BindingAdapter(value = ["cashWayColor"])
fun cashWayColor(textView: TextView,isShow:Boolean) {
    if(isShow)
    {
        textView.setBackgroundColor(
            ContextCompat.getColor(
                textView.context,
                R.color.colorPrimary
            )
        )
        textView.setTextColor(ContextCompat.getColor(textView.context, R.color.white))
    }
    else
    {
        textView.setTextColor(ContextCompat.getColor(textView.context,R.color.grey_100_))
        textView.setBackgroundColor(
            ContextCompat.getColor(
                textView.context,
                R.color.white
            )
        )
    }
}

