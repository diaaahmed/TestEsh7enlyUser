package com.esh7enly.data.sharedhelper

import androidx.datastore.preferences.core.stringPreferencesKey

object Constants
{
    val token_key = stringPreferencesKey("token_key")

    const val STORE_NAME = "store_name"

    const val IS_LOGIN = "isLogin"
    const val USER_NAME = "user_name"
    const val FIRST_START = "first_start"

    const val USER_PASSWORD = "user_password"
    const val USER_TOKEN = "user_token"
    const val USER_EMAIL = "user_email"
    const val APP_LANGUAGE = "APP_LANGUAGE"

    const val AR = "ar"

    const val IS_REMEMBER_ME = "is_remember_me"

    var LANG = "lang"

    const val CODE_HTTP_UNAUTHORIZED = "401"

}