package com.esh7enly.esh7enlyuser.util

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.annotation.RequiresApi
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.Enumeration
import javax.crypto.Cipher

@RequiresApi(Build.VERSION_CODES.M)
object EncryptionUtils {
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
   // private const val KEY_ALIAS = "MytestAlias" // As per your requirment

    fun generateKey(KEY_ALIAS:String): KeyPair?{
        val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
            load(null)
        }

        val aliases: Enumeration<String> = keyStore.aliases()
        val keyPair: KeyPair?

        if (aliases.toList().firstOrNull { it == KEY_ALIAS } == null){
            val keyPairGenerator: KeyPairGenerator = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_RSA,
                ANDROID_KEYSTORE
            )
            val parameterSpec: KeyGenParameterSpec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT )
                .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                .build()

            keyPairGenerator.initialize(parameterSpec)
            keyPair = keyPairGenerator.genKeyPair()
        }else{
            val entry = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.PrivateKeyEntry
            keyPair = KeyPair(entry?.certificate?.publicKey,entry?.privateKey)
        }
        return keyPair
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getPublicKey(KEY_ALIAS:String): String? {
        val keyPair = generateKey(KEY_ALIAS)
        val publicKey = keyPair?.public ?: return null
        return String(Base64.encode(publicKey.encoded, Base64.DEFAULT))
    }


    @RequiresApi(Build.VERSION_CODES.M)
    fun getPrivateKey(KEY_ALIAS:String): PrivateKey? {
        val keyPair = generateKey(KEY_ALIAS)
        return keyPair?.private
    }

    fun encrypt(data: String, publicKey: PublicKey): String {
        val cipher: Cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val bytes = cipher.doFinal(data.toByteArray())
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    fun decrypt(data: String, privateKey: PrivateKey?): String {
        val cipher: Cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        val encryptedData = Base64.decode(data, Base64.DEFAULT)
        val decodedData = cipher.doFinal(encryptedData)
        return String(decodedData)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun decodePublicKey(publicKey: String?): PublicKey {
        val decodedKey = Base64.decode(publicKey, Base64.DEFAULT)
        // Convert the byte array back to PublicKey object
        val keyFactory = KeyFactory.getInstance(KeyProperties.KEY_ALGORITHM_RSA)
        val keySpec = X509EncodedKeySpec(decodedKey)
        return keyFactory.generatePublic(keySpec)
    }
}



