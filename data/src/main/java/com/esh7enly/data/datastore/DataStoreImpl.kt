import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.esh7enly.data.sharedhelper.Constants
import com.esh7enly.domain.repo.SaveUserDataRepo
import com.esh7enly.esh7enlyuser.util.CryptoData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Base64

class DataStoreImpl(
    var context: Context,
    private var cryptoData: CryptoData

) : SaveUserDataRepo {

    private val Context.dataStore: DataStore<Preferences> by
    preferencesDataStore("data")

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun setStoreName(storeName: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            val encryptedToken =
                Base64.getEncoder().encodeToString(
                    cryptoData.encrypt(bytes = storeName!!.toByteArray()))

            context.dataStore.edit { preferences ->
                preferences[Constants.name_key] = encryptedToken
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getStoreName(): String? {
        return withContext(Dispatchers.IO) {
            val token = context.dataStore.data.map { preferences ->
                preferences[Constants.name_key]
            }.catch {

            }.firstOrNull()

            val originalText =
                cryptoData.decrypt(Base64.getDecoder().decode(token))?.decodeToString()

            return@withContext originalText
        }
    }

    override suspend fun setUserPassword(password: String?) {
        TODO("Not yet implemented")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun setUserToken(token: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            val encryptedToken =
                Base64.getEncoder().encodeToString(
                    cryptoData.encrypt(bytes = token!!.toByteArray()))

            context.dataStore.edit { preferences ->
                preferences[Constants.token_key] = encryptedToken
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getUserToken(): String? {
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

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun setUserPhone(userPhone: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            val encryptedToken =
                Base64.getEncoder().encodeToString(
                    cryptoData.encrypt(bytes = userPhone!!.toByteArray()))

            context.dataStore.edit { preferences ->
                preferences[Constants.phone_key] = encryptedToken
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getUserPhone(): String? {
        return withContext(Dispatchers.IO) {
            val token = context.dataStore.data.map { preferences ->
                preferences[Constants.phone_key]
            }.catch {

            }.firstOrNull()

            val originalText =
                cryptoData.decrypt(Base64.getDecoder().decode(token))?.decodeToString()

            return@withContext originalText
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun setUserName(userName: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            val encryptedToken =
                Base64.getEncoder().encodeToString(
                    cryptoData.encrypt(bytes = userName!!.toByteArray()))

            context.dataStore.edit { preferences ->
                preferences[Constants.name_key] = encryptedToken
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getUserName(): String? {
        return withContext(Dispatchers.IO) {
            val token = context.dataStore.data.map { preferences ->
                preferences[Constants.name_key]
            }.catch {

            }.firstOrNull()

            val originalText =
                cryptoData.decrypt(Base64.getDecoder().decode(token))?.decodeToString()

            return@withContext originalText
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun setUserEmail(email: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            val encryptedToken =
                Base64.getEncoder().encodeToString(
                    cryptoData.encrypt(bytes = email!!.toByteArray()))

            context.dataStore.edit { preferences ->
                preferences[Constants.email_key] = encryptedToken
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getUserEmail(): String? {
        return withContext(Dispatchers.IO) {
            val token = context.dataStore.data.map { preferences ->
                preferences[Constants.email_key]
            }.catch {

            }.firstOrNull()

            val originalText =
                cryptoData.decrypt(Base64.getDecoder().decode(token))?.decodeToString()

            return@withContext originalText
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun setAppLanguage(lang: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val encryptedToken =
                Base64.getEncoder().encodeToString(
                    cryptoData.encrypt(bytes = lang.toByteArray()))

            context.dataStore.edit { preferences ->
                preferences[Constants.language_key] = encryptedToken
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getAppLanguage(): String? {
        return withContext(Dispatchers.IO) {
            val token = context.dataStore.data.map { preferences ->
                preferences[Constants.language_key]
            }.catch {

            }.firstOrNull()

            val originalText =
                cryptoData.decrypt(Base64.getDecoder().decode(token))?.decodeToString()

            return@withContext originalText
        }
    }

}