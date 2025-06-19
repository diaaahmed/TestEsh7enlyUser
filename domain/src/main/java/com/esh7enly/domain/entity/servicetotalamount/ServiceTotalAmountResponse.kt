package com.esh7enly.domain.entity.servicetotalamount

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServiceTotalAmountResponse(
    @SerialName("code")
    val code: Int,
    @SerialName("data")
    val `data`: Data,
    @SerialName("message")
    val message: String,
    @SerialName("status")
    val status: Boolean
)

@Serializable
data class Data(
    @SerialName("agent")
    val agent: Int,
    @SerialName("amount")
    val amount: Int,
    @SerialName("code")
    val code: Int,
    @SerialName("merchant")
    val merchant: Int,
    @SerialName("message")
    val message: Any,
    @SerialName("paid_amount")
    val paidAmount: Int,
    @SerialName("service_charge")
    val serviceCharge: Int,
    @SerialName("status")
    val status: Boolean,
    @SerialName("system")
    val system: Int,
    @SerialName("total_amount")
    val totalAmount: Int
)