package com.esh7enly.domain.entity.searchresponse.newresponse


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommissionTier(
    @SerialName("agent")
    val agent: String,
    @SerialName("from")
    val from: String,
    @SerialName("merchant")
    val merchant: String,
    @SerialName("system")
    val system: String,
    @SerialName("to")
    val to: String,
    @SerialName("type")
    val type: String
)