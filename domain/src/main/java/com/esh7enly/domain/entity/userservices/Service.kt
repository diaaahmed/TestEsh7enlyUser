package com.esh7enly.domain.entity.userservices

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Service(
    val accept_amount_input: Int,
    val accept_change_paid_amount: Int,
    val accept_check_integration_provider_status: Int,
//    val description_ar: String,
//    val description_en: String,
    val icon: String,
    @PrimaryKey
    val id: Int,
    val name_ar: String,
    val name_en: String,
//    val powered_by_ar: Any,
//    val powered_by_en: Any,
    val price_max_value: String?,
    val price_min_value: String,
    val price_type: Int,
    val price_value: String,
    val price_value_list: String,
    val provider_id: Int,
    val sort: Int,
    val type: Int,
    val type_code: String,
    val provider_name_ar:String,
    var provider_name_en:String
)