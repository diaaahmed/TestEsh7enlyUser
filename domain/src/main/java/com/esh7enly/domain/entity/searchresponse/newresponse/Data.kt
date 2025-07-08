package com.esh7enly.domain.entity.searchresponse.newresponse


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Data(
    @SerialName("current_page")
    val currentPage: Int,
    @SerialName("data")
    val `data`: List<DataX>,
    @SerialName("first_page_url")
    val firstPageUrl: String,
    @SerialName("from")
    val from: Int,
    @SerialName("last_page")
    val lastPage: Int,
    @SerialName("last_page_url")
    val lastPageUrl: String,
    @SerialName("next_page_url")
    val nextPageUrl: Any,
    @SerialName("path")
    val path: String,
    @SerialName("per_page")
    val perPage: Int,
    @SerialName("prev_page_url")
    val prevPageUrl: Any,
    @SerialName("to")
    val to: Int,
    @SerialName("total")
    val total: Int
)