interface SaveUserDataRepo{

    // Save and Get store name
    suspend fun setStoreName(storeName: String?)
    suspend fun getStoreName(): String?

    // Save user password
    suspend fun setUserPassword(password: String?)

    // Save and Get User token
    suspend fun setUserToken(token: String?)
    suspend fun getUserToken(): String?

    // Save and Get User phone
    suspend fun setUserPhone(userPhone: String?)
    suspend fun getUserPhone():String?

    // Save and Get User name
    suspend fun setUserName(userName: String?)
    suspend fun getUserName(): String?

    // Save and Get User email
    suspend fun setUserEmail(email: String?)
    suspend fun getUserEmail():String?

    // Save and Get app Language
    suspend fun setAppLanguage(lang: String)
    suspend fun getAppLanguage():String?

}