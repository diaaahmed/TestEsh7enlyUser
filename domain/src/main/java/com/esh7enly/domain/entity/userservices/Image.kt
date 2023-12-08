package com.esh7enly.domain.entity.userservices

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Image(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val path: String,
    val service_id: Int
)