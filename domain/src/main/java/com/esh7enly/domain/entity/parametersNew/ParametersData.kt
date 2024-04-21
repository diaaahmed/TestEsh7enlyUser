package com.esh7enly.domain.entity.parametersNew

import com.esh7enly.domain.entity.userservices.TypeValue

data class ParametersData(
    val created_at: String,
    val default_value: Any,
    val display: Int,
    val external_id: String,
    val fillable: Int,
    val id: Int,
    val internal_id: String,
    val is_client_number: Int,
    val max_length: Int,
    val min_length: Int,
    val name_ar: String,
    val name_en: String,
    val required: Int,
    val service_id: Int,
    val sort: Int,
    val type: Int,
    val type_values: List<TypeValue>,
    val updated_at: String,
    val validation: Any,
    val visible: Int
)