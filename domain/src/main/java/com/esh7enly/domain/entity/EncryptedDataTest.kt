package com.esh7enly.domain.entity


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EncryptedDataTest(
    @SerialName("encryptedData")
    val encryptedData: String
)