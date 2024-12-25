package com.esh7enly.data.datastore

import android.annotation.SuppressLint
import androidx.datastore.core.Serializer
import com.esh7enly.esh7enlyuser.util.CryptoData
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream
import java.util.Base64

object UserSerializer: Serializer<UserDataStore> {

    val cryptoData = CryptoData()

    override val defaultValue: UserDataStore
        get() = UserDataStore()

    @SuppressLint("NewApi")
    override suspend fun writeTo(
        t: UserDataStore,
        output: OutputStream
    ) {
        val jsonUser = Gson().toJson(t)

        val bytes = jsonUser.toByteArray()

        val encryptedUser = Base64.getEncoder()
            .encodeToString(cryptoData.encrypt(bytes = bytes))


        withContext(Dispatchers.IO)
        {
           // output.write(jsonUser.toByteArray())
            output.write(encryptedUser.toByteArray())

        }
    }

    @SuppressLint("NewApi")
    override suspend fun readFrom(input: InputStream): UserDataStore
    {
        val data = input.readBytes().decodeToString()
        val originalUSer = cryptoData.decrypt(
            Base64.getDecoder().decode(data)
        )?.decodeToString()

      //  return Gson().fromJson(input.readBytes().decodeToString(), UserDataStore::class.java)
        return Gson().fromJson(originalUSer, UserDataStore::class.java)
    }

}