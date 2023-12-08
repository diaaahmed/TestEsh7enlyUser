package com.esh7enly.domain.entity.userservices

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Parameter(
    val display: Int,
    @PrimaryKey
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
    val type_values: List<TypeValue>
)