package com.example.sookwalk.data.local.entity.map

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_categories")
data class FavoriteCategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val iconColor: Long,
    val createdAt: Long = System.currentTimeMillis()
)