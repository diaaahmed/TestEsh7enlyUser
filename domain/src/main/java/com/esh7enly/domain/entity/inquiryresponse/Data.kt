package com.esh7enly.domain.entity.inquiryresponse

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Data(
    @SerialName("agent_commission")
    val agentCommission: Any,
    @SerialName("amount")
    val amount: Double,
    @SerialName("balance_after")
    val balanceAfter: Int,
    @SerialName("balance_before")
    val balanceBefore: Int,
    @SerialName("client_number")
    val clientNumber: String,
    @SerialName("description")
    val description: String,
    @SerialName("external_transaction_id")
    val externalTransactionId: String,
    @SerialName("extra_data")
    val extraData: List<Any>,
    @SerialName("extra_system_commission")
    val extraSystemCommission: Any,
    @SerialName("first_parent_merchant_id")
    val firstParentMerchantId: Any,
    @SerialName("id")
    val id: Int,
    @SerialName("imei")
    val imei: String,
    @SerialName("inquiry_transaction_id")
    val inquiryTransactionId: String,
    @SerialName("integration_provider")
    val integrationProvider: IntegrationProvider,
    @SerialName("integration_provider_amount")
    val integrationProviderAmount: Double,
    @SerialName("integration_provider_balance")
    val integrationProviderBalance: String,
    @SerialName("integration_provider_commission")
    val integrationProviderCommission: Any,
    @SerialName("is_paid")
    val isPaid: Any,
    @SerialName("is_settled")
    val isSettled: Int,
    @SerialName("max_amount")
    val maxAmount: Double,
    @SerialName("merchant")
    val merchant: Merchant,
    @SerialName("merchant_commission")
    val merchantCommission: String,
    @SerialName("message")
    val message: String,
    @SerialName("min_amount")
    val minAmount: Double,
    @SerialName("paid_amount")
    val paidAmount: Double,
    @SerialName("parameters")
    val parameters: List<Parameter>,
    @SerialName("parent_merchant_id")
    val parentMerchantId: String,
    @SerialName("provider_transaction_id")
    val providerTransactionId: String,
    @SerialName("request")
    val request: String,
    @SerialName("request_duration")
    val requestDuration: Int,
    @SerialName("request_map")
    val requestMap: RequestMap,
    @SerialName("response")
    val response: String,
    @SerialName("service")
    val service: Service,
    @SerialName("service_charge")
    val serviceCharge: Double,
    @SerialName("settlement_type")
    val settlementType: Int,
    @SerialName("settlement_wallet_transaction_id")
    val settlementWalletTransactionId: Any,
    @SerialName("staff_id")
    val staffId: Int,
    @SerialName("status")
    val status: Int,
    @SerialName("status_code")
    val statusCode: Int,
    @SerialName("system_commission")
    val systemCommission: String,
    @SerialName("total_amount")
    val totalAmount: Double,
    @SerialName("type")
    val type: String,
    @SerialName("validation_error")
    val validationError: Any
)