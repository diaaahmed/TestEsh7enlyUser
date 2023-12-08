package com.esh7enly.domain.entity.userservices

data class Data(
    val categories: List<Category>,
    val images: List<Image>,
    val parameters: List<Parameter>,
    val providers: List<Provider>,
    val services: List<Service>
)