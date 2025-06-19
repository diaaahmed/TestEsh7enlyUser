package com.esh7enly.esh7enlyuser.util

import android.content.Context
import android.util.Base64
import java.io.InputStream
import java.security.KeyFactory
import java.security.PublicKey
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.spec.X509EncodedKeySpec

import javax.crypto.Cipher

fun getPublicKeyFromPemWithCertificate(context: Context, pemFileName: String): PublicKey {
    // Load the PEM file from assets
    val inputStream: InputStream = context.assets.open(pemFileName)
    val pemContent = inputStream.bufferedReader().use { it.readText() }

    // Remove the PEM header and footer
    val publicKeyPEMFormatted = pemContent
        .replace("-----BEGIN CERTIFICATE-----", "")
        .replace("-----END CERTIFICATE-----", "")
        .replace("\\s+".toRegex(), "") // Remove whitespace

    // Decode the Base64 encoded string
    val keyBytes = Base64.decode(publicKeyPEMFormatted, Base64.DEFAULT)

    // Create a CertificateFactory
    val certificateFactory = CertificateFactory.getInstance("X.509")
    val certificate = certificateFactory.generateCertificate(keyBytes.inputStream()) as X509Certificate

    // Extract the public key
    return certificate.publicKey
}

fun getPublicKeyFromPemLastWithPublic(context: Context, pemFileName: String): PublicKey {
    val inputStream: InputStream = context.assets.open(pemFileName)
    val pemContent = inputStream.bufferedReader().use { it.readText() }
    // Remove the header and footer
    val publicKeyPEMFormatted = pemContent
        .replace("-----BEGIN PUBLIC KEY-----", "")
        .replace("-----END PUBLIC KEY-----", "")
        .replace("\\s+".toRegex(), "") // Remove whitespace

    // Decode the Base64 encoded string
    val keyBytes = Base64.decode(publicKeyPEMFormatted, Base64.DEFAULT)

    // Generate the public key
    val keySpec = X509EncodedKeySpec(keyBytes)
    val keyFactory = KeyFactory.getInstance("RSA")
    return keyFactory.generatePublic(keySpec)
}

fun encryptDataWithPublicKey(data: String, publicKey: PublicKey): String {
    val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
    cipher.init(Cipher.ENCRYPT_MODE, publicKey)

    val encryptedBytes = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
    return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
}

