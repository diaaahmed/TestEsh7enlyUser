package com.esh7enly.domain.entity.searchresponse.newresponse


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewSearchResponse(
    @SerialName("code")
    val code: Int,
    @SerialName("data")
    val `data`: Data?,
    @SerialName("message")
    val message: String,
    @SerialName("status")
    val status: Boolean?
)