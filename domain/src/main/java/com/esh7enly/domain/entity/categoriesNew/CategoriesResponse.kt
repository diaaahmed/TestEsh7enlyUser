package com.esh7enly.domain.entity.categoriesNew

data class CategoriesResponse(
    val code: Int,
    val `data`: List<CategoryData>,
    val message: String,
    val service_update_num: String,
    val status: Boolean
)