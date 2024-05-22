package com.esh7enly.esh7enlyuser.util

import com.google.firebase.crashlytics.FirebaseCrashlytics

object CrashlyticsUtils {

    const val LOGIN_KEY = "LOGIN_KEY"
    const val LOGIN_PROVIDER = "LOGIN_PROVIDER"

    // generic method to avoid create a log in one crash with a new variant and create a new separate crashlytics log
    inline fun <reified T : Exception> sendCustomLogToCrashlytics(
        msg: String, vararg keys: Pair<String, String>
    ) {
        keys.forEach { key ->
            FirebaseCrashlytics.getInstance().setCustomKey(key.first, key.second)
        }

        val exception = T::class.java.getConstructor(String::class.java).newInstance(msg)
        FirebaseCrashlytics.getInstance().recordException(exception)
    }

}

class LoginException(message: String) : Exception(message)
class CrashMessage(message: String) : Exception(message)
