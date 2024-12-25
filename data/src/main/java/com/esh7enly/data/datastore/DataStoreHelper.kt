package com.esh7enly.data.datastore

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.esh7enly.data.sharedhelper.Constants
import com.esh7enly.esh7enlyuser.util.CryptoData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Base64
import kotlin.text.decodeToString
import kotlin.text.toByteArray


class DataStoreHelper(var context: Context,
                      var cryptoData: CryptoData
) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("data")

    private val Context.dataStoreProto by dataStore("dataproto.json", UserSerializer)

    fun saveUserData(userDataStore: UserDataStore) {
        CoroutineScope(Dispatchers.IO).launch {
            context.dataStoreProto.updateData {
                it.copy(
                    token = userDataStore.token,
                    userName = userDataStore.userName,
                    userEmail = userDataStore.userEmail,
                    userPhone = userDataStore.userPhone
                )
            }
        }
    }

    @SuppressLint("NewApi")
    suspend fun getUserData(): UserDataStore? {

        return withContext(Dispatchers.IO) {
            val userData = context.dataStoreProto.data.onEach {
               it
            }.catch {

            }.firstOrNull()

            return@withContext userData
        }

    }

    @SuppressLint("NewApi")
    fun saveTokenKey(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            var encryptedToken =
                Base64.getEncoder().encodeToString(cryptoData.encrypt(bytes = token.toByteArray()))

            Log.d("TAG", "dataStore: Main token $token")
            Log.d("TAG", "dataStore: encryptedToken $encryptedToken")

            context.dataStore.edit { preferences ->
                preferences[Constants.token_key] = encryptedToken
            }
        }
    }

    // Optionally, you can add a method to retrieve the token
    @SuppressLint("NewApi")
    suspend fun getTokenKey(): String? {
        return withContext(Dispatchers.IO) {
            val token = context.dataStore.data.map { preferences ->
                preferences[Constants.token_key]
            }.catch {

            }.firstOrNull()

            val originalText =
                cryptoData.decrypt(Base64.getDecoder().decode(token))?.decodeToString()

            return@withContext originalText
        }
    }

}