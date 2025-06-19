package com.esh7enly.domain.entity.imageadsresponse

data class ImageAdResponse(
    val code: Int,
    val `data`: Data,
    val message: String,
    val status: Boolean
)

data class Data(
    val current_page: Int,
    val `data`: List<DataX>,
    val last_page: Int,
    val per_page: Int,
    val total: Int
)

data class DataX(
    val banner: String,
    val created_at: String,
    val id: Int
)