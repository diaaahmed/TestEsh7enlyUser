package com.esh7enly.domain.entity.userservices

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Provider(
    val category_id: Int,
    @PrimaryKey
    val id: Int,
    val logo: String,
    val name_ar: String,
    val name_en: String,
    val sort: Int
)