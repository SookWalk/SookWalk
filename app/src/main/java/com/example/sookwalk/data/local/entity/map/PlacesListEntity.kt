package com.example.sookwalk.data.local.entity.map

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_place")
data class FavoritePlaceEntity(
    @PrimaryKey val placeId: String,    // Google Place ID
    val name: String,
    val address: String?,
    val photoReference: String?, // 사진 URL을 만들 수 있는 참조 1개
    val primaryType: String?,   // "한식", "카페" 등

    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorite_category")
data class FavoriteCategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val categoryId: Long = 0L,
    val name: String,      // 예: "숙명여대 맛집"
    val iconName: String,  // 예: "blue_star" (아이콘 매핑용)
    val count: Int = 0     // 예: 60 (이건 DAO에서 계산)
)

@Entity(
    tableName = "category_place_cross_ref",
    primaryKeys = ["categoryId", "placeId"] // 두 ID의 조합이 고유해야 함
)
data class CategoryPlaceCrossRef(
    val categoryId: Long,
    val placeId: String
)