package com.esh7enly.domain.entity.parametersNew

data class ParametersResponse(
    val code: Int,
    val `data`: List<ParametersData>,
    val message: String,
    val service_update_num: String,
    val status: Boolean
)