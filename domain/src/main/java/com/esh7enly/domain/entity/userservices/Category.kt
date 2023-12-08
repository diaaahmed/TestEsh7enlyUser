package com.esh7enly.domain.entity.userservices

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Category(
    val icon: String,
    @PrimaryKey
    val id: Int,
    val name_ar: String,
    val name_en: String,
    val sort: Int
)