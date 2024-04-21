package com.esh7enly.domain.entity.servicesNew


data class ServiceResponse(
    val code: Int,
    val `data`: List<ServiceData>,
    val message: String,
    val service_update_num: String,
    val status: Boolean
)