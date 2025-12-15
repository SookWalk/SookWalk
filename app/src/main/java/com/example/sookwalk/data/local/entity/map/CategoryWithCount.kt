package com.example.sookwalk.data.local.entity.map

import androidx.room.Embedded

data class CategoryWithCount(
    @Embedded val category: FavoriteCategoryEntity,

    val placeCount: Int
)