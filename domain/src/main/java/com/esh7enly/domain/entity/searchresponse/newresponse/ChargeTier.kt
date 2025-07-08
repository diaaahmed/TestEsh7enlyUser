package com.esh7enly.domain.entity.searchresponse.newresponse


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChargeTier(
    @SerialName("amount")
    val amount: String,
    @SerialName("from")
    val from: String,
    @SerialName("to")
    val to: String,
    @SerialName("type")
    val type: String
)