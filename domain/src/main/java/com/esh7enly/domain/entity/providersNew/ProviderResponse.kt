package com.esh7enly.domain.entity.providersNew

data class ProviderResponse(
    val code: Int,
    val `data`: List<ProviderData>,
    val message: String,
    val service_update_num: String,
    val status: Boolean
)