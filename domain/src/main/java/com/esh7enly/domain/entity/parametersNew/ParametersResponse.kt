package com.esh7enly.domain.entity.parametersNew

import com.esh7enly.domain.entity.userservices.TypeValue

data class ParametersResponse(
    val code: Int,
    val message: String,
    val status: Boolean = false,
    val `data`: List<ParametersData> = emptyList())

data class ParametersData(
    val visible: Int,
    val name:String,
    val default_value: Any,
    val display: Int,
    val external_id: String,
    val fillable: Int,
    val id: Int,
    val internal_id: String,
    val is_client_number: Int,
    val max_length: Int,
    val min_length: Int,
    val required: Int,
    val service_id: Int,
    val type: Int,
    val type_values: List<TypeValue>,
)