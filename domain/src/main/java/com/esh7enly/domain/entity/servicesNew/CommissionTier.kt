package com.esh7enly.domain.entity.servicesNew

data class CommissionTier(
    val agent: String,
    val from: String,
    val merchant: String,
    val system: String,
    val to: String,
    val type: String
)