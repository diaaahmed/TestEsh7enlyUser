package com.esh7enly.data.sharedhelper

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import com.esh7enly.data.R

class SharedHelper(
    var context: Context,
    private var sharedPreferencesEncrypted: SharedPreferences
) {

    private val data = "data"

    fun saveCardID(cardID: String?) {
        with(sharedPreferencesEncrypted.edit())
        {
            putString(Constants.CARD_ID, cardID)
            apply()
        }
    }

    fun getCardID(): String? =
        sharedPreferencesEncrypted.getString(Constants.CARD_ID, null)

    fun savePayToken(token: String?) {
        with(sharedPreferencesEncrypted.edit())
        {
            putString(Constants.PAY_TOKEN, token)
            apply()
        }
    }

    fun getPayToken(): String? =
        sharedPreferencesEncrypted.getString(Constants.PAY_TOKEN, null)

    fun saveTransactionRef(transactionRef: String?) {
        with(sharedPreferencesEncrypted.edit())
        {
            putString(Constants.TRANSACTION_REF, transactionRef)
            apply()
        }
    }

    fun getTransactionRef(): String? =
        sharedPreferencesEncrypted.getString(Constants.TRANSACTION_REF, null)


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

    }

    fun getUserToken(): String =
        "Bearer " + sharedPreferencesEncrypted.getString(Constants.USER_TOKEN, "")

    fun getNormalToken(): String =
        sharedPreferencesEncrypted.getString(Constants.USER_TOKEN, "") ?: ""

    fun getDataToken(): String {
        return getUserToken()
            .replace("Bearer ", "")
            .take(16)
    }

    fun setUserPassword(password: String) {
        sharedPref.edit().putString(Constants.USER_PASSWORD, encrypt(password)).apply()
    }

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