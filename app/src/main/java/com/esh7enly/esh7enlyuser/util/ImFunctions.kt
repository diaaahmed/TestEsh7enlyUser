package com.esh7enly.esh7enlyuser.util

fun sendIssueToCrashlytics(msg: String, functionName: String) {
    CrashlyticsUtils.sendCustomLogToCrashlytics<CrashMessage>(
        msg,
        CrashlyticsUtils.LOGIN_KEY to msg,
        CrashlyticsUtils.LOGIN_PROVIDER to functionName,
    )
}