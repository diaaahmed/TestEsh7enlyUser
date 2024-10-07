package com.esh7enly.domain.entity.servicesNew

import com.google.gson.annotations.SerializedName

data class ServiceData(
    @SerializedName("accept_amount_input")
    val acceptAmountInput: Int,
    @SerializedName("accept_cancel")
    val acceptCancel: Int,
    @SerializedName("accept_change_paid_amount")
    val acceptChangePaidAmount: Int,
    @SerializedName("accept_check_integration_provider_status")
    val acceptCheckIntegrationProviderStatus: Int,
    @SerializedName("alternative_service_id")
    val alternativeServiceId: Any?,
    @SerializedName("charge_tiers")
    val chargeTierList: List<ChargeTier>,
    @SerializedName("charge_type")
    val chargeType: Int,
    @SerializedName("commission_category_id")
    val commissionCategoryId: Int,
    @SerializedName("commission_tiers")
    val commissionTierList: List<CommissionTier>,
    @SerializedName("connector_path")
    val connectorPath: String,
    @SerializedName("convert_services")
    val convertServices: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("deleted_at")
    val deletedAt: Any?,
    @SerializedName("description_ar")
    val descriptionAr: String,
    @SerializedName("description_en")
    val descriptionEn: String,
    @SerializedName("display_total_amount")
    val displayTotalAmount: Int,
    @SerializedName("external_id")
    val externalId: String,
    @SerializedName("extra_commission_tiers")
    val extraCommissionTierList: List<ExtraCommissionTier>,
    @SerializedName("footer_description_ar")
    val footerDescriptionAr: String,
    @SerializedName("footer_description_en")
    val footerDescriptionEn: String,
    @SerializedName("grace_period")
    val gracePeriod: Int,
    val icon: String,
    val id: Int,
    @SerializedName("integration_provider_id")
    val integrationProviderId: Int,
    @SerializedName("integration_provider_tiers")
    val integrationProviderTierList: List<IntegrationProviderTier>,
    @SerializedName("name_ar")
    val nameAr: String,
    @SerializedName("name_en")
    val nameEn: String,
    @SerializedName("platform_ids")
    val platformIds: List<String>,
    @SerializedName("powered_by_ar")
    val poweredByAr: String,
    @SerializedName("powered_by_en")
    val poweredByEn: String,
    @SerializedName("price_max_value")
    val priceMaxValue: String,
    @SerializedName("price_min_value")
    val priceMinValue: String,
    @SerializedName("price_type")
    val priceType: Int,
    @SerializedName("price_value")
    val priceValue: String,
    @SerializedName("price_value_list")
    val priceValueList: String,
    @SerializedName("rule_path")
    val rulePath: Any?,
    @SerializedName("rule_settings")
    val ruleSettings: Any?,
    @SerializedName("rule_status")
    val ruleStatus: Int,
    @SerializedName("service_provider_id")
    val serviceProviderId: Int,
    val sort: Int,
    @SerializedName("staff_id")
    val staffId: Int,
    val status: Int,
    val type: Int,
    @SerializedName("type_code")
    val typeCode: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    val visible: Int,
    @SerializedName("wallet_type_id")
    val walletTypeId: Int
)