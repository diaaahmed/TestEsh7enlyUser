package com.esh7enly.domain.entity.searchresponse.newresponse


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DataX(
    @SerialName("accept_amount_input")
    val acceptAmountInput: Int,
    @SerialName("accept_change_paid_amount")
    val acceptChangePaidAmount: Int,
    @SerialName("accept_check_integration_provider_status")
    val acceptCheckIntegrationProviderStatus: Int,
    @SerialName("charge_tiers")
    val chargeTiers: List<ChargeTier>,
    @SerialName("commission_tiers")
    val commissionTiers: List<CommissionTier>,
    @SerialName("description")
    val description: Any,
    @SerialName("icon")
    val icon: String,
    @SerialName("id")
    val id: Int,
    @SerialName("integration_provider_id")
    val integrationProviderId: Int,
    @SerialName("name")
    val name: String,
    @SerialName("price_min_value")
    val priceMinValue: String,
    @SerialName("price_type")
    val priceType: Int,
    @SerialName("price_value")
    val priceValue: String,
    @SerialName("price_value_list")
    val priceValueList: String,
    @SerialName("service_provider_id")
    val serviceProviderId: Int,
    @SerialName("sort")
    val sort: Int,
    @SerialName("type")
    val type: Int,
    @SerialName("type_code")
    val typeCode: String
)