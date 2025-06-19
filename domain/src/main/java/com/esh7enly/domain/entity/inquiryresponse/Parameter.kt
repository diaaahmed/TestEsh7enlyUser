package com.esh7enly.domain.entity.inquiryresponse


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Parameter(
    @SerialName("display_name")
    val displayName: String,
    @SerialName("internal_id")
    val internalId: String,
    @SerialName("key")
    val key: String,
    @SerialName("value")
    val value: String
)