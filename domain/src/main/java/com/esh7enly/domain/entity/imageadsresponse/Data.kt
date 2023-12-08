package com.esh7enly.domain.entity.imageadsresponse

data class Data(
    val current_page: Int,
    val `data`: List<DataX>,
    val last_page: Int,
    val per_page: Int,
    val total: Int
)