package com.esh7enly.domain.entity.inquiryresponse


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IntegrationProvider(
    @SerialName("description")
    val description: Any,
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String
)