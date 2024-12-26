package com.esh7enly.data.sharedhelper

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import com.esh7enly.data.R

class SharedHelper(var context: Context, var sharedPreferencesEncrypted: SharedPreferences ) {

    private val data = "data"

    private var sharedPref = context.getSharedPreferences(data, Context.MODE_PRIVATE)

    fun setRememberPassword(boolean: Boolean) {
        sharedPref.edit().putBoolean(Constants.IS_REMEMBER_ME, boolean).apply()
    }

    fun isRememberUser(boolean: Boolean) {
        sharedPref.edit().putBoolean(Constants.IS_LOGIN, boolean).apply()
    }


    fun setAppLanguage(lang: String) {
        sharedPref.edit().putString(Constants.APP_LANGUAGE, lang).apply()
    }

    fun getAppLanguage(): String? =
        sharedPref.getString(Constants.APP_LANGUAGE, "ar")


    fun setUserEmail(email: String?) {
        sharedPref.edit().putString(Constants.USER_EMAIL, email).apply()
    }

    fun getUserEmail(): String? =
        sharedPref.getString(
            Constants.USER_EMAIL,
            "esh7enly@gmail.com"
        )


    fun setUserName(userName: String) {
        sharedPref.edit().putString(Constants.USER_NAME, userName).apply()
    }

    fun setFirstOpen(isFirst: Boolean) {
        sharedPref.edit().putBoolean(Constants.FIRST_START, isFirst).apply()
    }

    fun isFirstOpen() = sharedPref.getBoolean(Constants.FIRST_START, true)

    fun getUserName(): String? = sharedPref.getString(
        Constants.USER_NAME,
        ""
    )

    fun getUserPhone(): String? =
        sharedPref.getString(
            Constants.USER_NAME,
            ""
        )

    fun setUserPhone(userPhone: String) {
        sharedPref.edit().putString(Constants.USER_NAME, userPhone).apply()

    }

    fun setUserToken(token: String) {

        with(sharedPreferencesEncrypted.edit())
        {
            putString(Constants.USER_TOKEN, token)
            apply()
        }

     //   sharedPref.edit().putString(Constants.USER_TOKEN, encrypt(token)).apply()
    }

   //  fun getUserToken(): String = "Bearer " + decrypt(sharedPref.getString(Constants.USER_TOKEN, ""))
     fun getUserToken(): String = "Bearer " + sharedPreferencesEncrypted.getString(Constants.USER_TOKEN, "")

    fun isRememberPassword(): Boolean = sharedPref.getBoolean(Constants.IS_REMEMBER_ME, false)

    fun setUserPassword(password: String) {
        sharedPref.edit().putString(Constants.USER_PASSWORD, encrypt(password)).apply()
    }

    fun getUserPassword(): String = decrypt(sharedPref.getString(Constants.USER_PASSWORD, ""))

    fun setStoreName(storeName: String) {
        sharedPref.edit().putString(Constants.STORE_NAME, storeName).apply()
    }

    fun getStoreName(): String? =
        sharedPref.getString(Constants.STORE_NAME, R.string.default_name.toString())

    //endregion

    //region Encrypt
    private fun encrypt(input: String): String? {
        return Base64.encodeToString(input.toByteArray(), 0)
    }

    private fun decrypt(input: String?): String {
        return String(Base64.decode(input, 0))
    }
}