package com.esh7enly.domain.entity.transactionresponse

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionsResponse(
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
    @SerialName("current_page")
    val currentPage: Int,
    @SerialName("data")
    val `data`: List<DataX>,
    @SerialName("last_page")
    val lastPage: Int,
    @SerialName("per_page")
    val perPage: Int,
    @SerialName("total")
    val total: Int
)

@Serializable
data class DataX(
    @SerialName("amount")
    val amount: String,
    @SerialName("client_number")
    val clientNumber: String,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("id")
    val id: Int,
    @SerialName("merchant_commission")
    val merchantCommission: String,
    @SerialName("message")
    val message: String,
    @SerialName("paid_amount")
    val paidAmount: String,
    @SerialName("provider")
    val provider: Provider,
    @SerialName("service")
    val service: Service,
    @SerialName("status")
    val status: Int,
    @SerialName("status_code")
    val statusCode: Int,
    @SerialName("total_amount")
    val totalAmount: String,
    @SerialName("type")
    val type: Int
)

@Serializable
data class Provider(
    @SerialName("id")
    val id: Int,
    @SerialName("logo")
    val logo: String,
    @SerialName("name")
    val name: String
)

@Serializable
data class Service(
    @SerialName("icon")
    val icon: String,
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String
)