package com.esh7enly.domain.entity.searchresponse

data class SearchResponse(
    val code: Int,
    val `data`: Data? = null,
    val message: String,
    val service_update_num: String = "",
    val status: Boolean = false
)