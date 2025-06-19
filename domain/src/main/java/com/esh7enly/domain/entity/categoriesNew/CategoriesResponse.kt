package com.esh7enly.domain.entity.categoriesNew

data class CategoriesResponse(
    val code: Int,
    val `data`: List<CategoryData> = emptyList(),
    val message: String,
    val status: Boolean = false
)

data class CategoryData(
    val icon: String = "",
    val id: Int = 0,
    val name:String = "",
)