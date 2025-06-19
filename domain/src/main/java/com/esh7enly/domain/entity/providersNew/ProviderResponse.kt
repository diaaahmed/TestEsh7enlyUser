package com.esh7enly.domain.entity.providersNew

data class ProviderResponse(
    val code: Int,
    val `data`: List<ProviderData> = emptyList(),
    val message: String,
    val status: Boolean = false
)

data class ProviderData(
    val id: Int,
    val logo: String,
    val name: String
)