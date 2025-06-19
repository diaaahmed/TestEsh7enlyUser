package com.esh7enly.domain.entity.servicesNew

import com.google.gson.annotations.SerializedName


data class ServiceResponse(
    val code: Int,
    @SerializedName("data")
    val data: List<ServiceData> = emptyList(),
    val message: String,
    val status: Boolean = false
)