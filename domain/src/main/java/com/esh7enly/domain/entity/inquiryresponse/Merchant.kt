package com.esh7enly.domain.entity.inquiryresponse


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Merchant(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("store_name")
    val storeName: String
)