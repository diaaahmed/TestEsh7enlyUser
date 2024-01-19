package com.esh7enly.esh7enlyuser.click

interface OnResponseListener
{
    fun onSuccess(code: Int, msg: String?, obj: Any?)
    fun onFailed(code: Int, msg: String?)
}