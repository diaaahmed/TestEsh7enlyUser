package com.esh7enly.esh7enlyuser.util

import android.util.Base64
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey


fun generateAESKey(): SecretKey {
    val keyGen = KeyGenerator.getInstance("AES")
    keyGen.init(256) // You can use 128, 192, or 256 bits
    return keyGen.generateKey()
}
fun encryptWithAESKey(secretKey: SecretKey, data: String): String {
    val cipher = Cipher.getInstance("AES")
    cipher.init(Cipher.ENCRYPT_MODE, secretKey)
    val encryptedBytes = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
    return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
}

fun encryptAESKeyWithRSA(publicKey: PublicKey, secretKey: SecretKey): String {
    val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
    cipher.init(Cipher.ENCRYPT_MODE, publicKey)
    val encryptedKey = cipher.doFinal(secretKey.encoded)
    return Base64.encodeToString(encryptedKey, Base64.DEFAULT)
}

fun combineEncryptedData(encryptedAESKey: String, encryptedData: String): String {
    return "$encryptedAESKey:$encryptedData"
}


fun getPublicKeyFromString(key: String): PublicKey {
    // Remove headers and footers if present
    val cleanedKey = stripPublicKeyHeaders(key)

    // Decode the base64 encoded key
    val keyBytes = Base64.decode(cleanedKey, Base64.DEFAULT)

    // Create the public key from the encoded key bytes
    val spec = X509EncodedKeySpec(keyBytes)
    val keyFactory = KeyFactory.getInstance("RSA")
    return keyFactory.generatePublic(spec)
}



fun stripPublicKeyHeaders(key: String): String {
    return key
        .replace("-----BEGIN PUBLIC KEY-----", "")
        .replace("-----END PUBLIC KEY-----", "")
        .replace("\\s".toRegex(), "") // Remove any whitespace
}

fun encryptDataChat(publicKey: PublicKey, data: String): ByteArray {
    val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
    cipher.init(Cipher.ENCRYPT_MODE, publicKey)
    return cipher.doFinal(data.toByteArray(Charsets.UTF_8))
}

fun encryptAndEncodeDataChat(publicKey: PublicKey, data: String): String {
    val encryptedData = encryptDataChat(publicKey, data)
    return Base64.encodeToString(encryptedData, Base64.DEFAULT)
}