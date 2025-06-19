package com.esh7enly.domain.entity.inquiryresponse


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Service(
    @SerialName("category")
    val category: Category,
    @SerialName("description")
    val description: Any,
    @SerialName("footer_description")
    val footerDescription: Any,
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("powered_by")
    val poweredBy: String,
    @SerialName("provider")
    val provider: Provider,
    @SerialName("type")
    val type: Int
)