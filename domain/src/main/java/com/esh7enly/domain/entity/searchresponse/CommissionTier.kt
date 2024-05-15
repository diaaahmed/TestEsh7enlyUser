package com.esh7enly.domain.entity.searchresponse

data class CommissionTier(
    val agent: String,
    val from: String,
    val merchant: String,
    val system: String,
    val to: String,
    val type: String
)