package com.esh7enly.data.sharedhelper

import SaveUserDataRepo
import android.content.SharedPreferences
import com.esh7enly.data.R

class SharedPrefImpl(private var sharedPreferencesEncrypted: SharedPreferences): SaveUserDataRepo {

    override suspend fun setStoreName(storeName: String?) {
        with(sharedPreferencesEncrypted.edit())
        {
            putString(Constants.STORE_NAME, storeName)
            apply()
        }

    }

    override suspend  fun getStoreName(): String? =
        sharedPreferencesEncrypted.getString(Constants.STORE_NAME, R.string.default_name.toString())

    override suspend fun setUserPassword(password: String?) {
        with(sharedPreferencesEncrypted.edit())
        {
            putString(Constants.USER_PASSWORD, password)
            apply()
        }
    }

    override suspend fun setUserToken(token: String?) {
        with(sharedPreferencesEncrypted.edit())
        {
            putString(Constants.USER_TOKEN, token)
            apply()
        }
    }

    override suspend fun getUserToken(): String = "Bearer " + sharedPreferencesEncrypted.getString(Constants.USER_TOKEN, "")


    override suspend fun setUserPhone(userPhone: String?) {
        with(sharedPreferencesEncrypted.edit())
        {
            putString(Constants.USER_NAME, userPhone)
            apply()
        }
    }

    override suspend fun getUserPhone(): String? =
        sharedPreferencesEncrypted.getString(
            Constants.USER_NAME,
            ""
        )

    override suspend fun setUserName(userName: String?) {
        with(sharedPreferencesEncrypted.edit())
        {
            putString(Constants.USER_NAME, userName)
            apply()
        }
    }

    override suspend fun getUserName(): String? = sharedPreferencesEncrypted.getString(
        Constants.USER_NAME,
        ""
    )

    override suspend fun setUserEmail(email: String?) {
        with(sharedPreferencesEncrypted.edit())
        {
            putString(Constants.USER_EMAIL, email)
            apply()
        }
    }

    override suspend fun getUserEmail(): String? =
        sharedPreferencesEncrypted.getString(
            Constants.USER_EMAIL,
            "esh7enly@gmail.com"
        )

    override suspend fun setAppLanguage(lang: String) {
        with(sharedPreferencesEncrypted.edit())
        {
            putString(Constants.APP_LANGUAGE, lang)
            apply()
        }
    }

    override suspend fun getAppLanguage(): String? =
        sharedPreferencesEncrypted.getString(Constants.APP_LANGUAGE, "ar")

}