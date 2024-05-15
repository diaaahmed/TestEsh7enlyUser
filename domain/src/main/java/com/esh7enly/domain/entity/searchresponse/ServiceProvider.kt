package com.esh7enly.domain.entity.searchresponse

data class ServiceProvider(
    val created_at: String,
    val deleted_at: Any,
    val description_ar: Any,
    val description_en: Any,
    val id: Int,
    val logo: String,
    val name_ar: String,
    val name_en: String,
    val service_category: ServiceCategory,
    val service_category_id: Int,
    val sort: Int,
    val staff_id: Int,
    val status: Int,
    val updated_at: String
)