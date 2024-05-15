package com.esh7enly.domain.entity.searchresponse

data class ServiceCategory(
    val created_at: String,
    val description_ar: Any,
    val description_en: Any,
    val icon: String,
    val id: Int,
    val name_ar: String,
    val name_en: String,
    val sort: Int,
    val staff_id: Int,
    val status: Int,
    val updated_at: String
)