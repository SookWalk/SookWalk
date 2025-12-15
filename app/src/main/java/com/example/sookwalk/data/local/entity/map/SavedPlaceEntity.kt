package com.example.sookwalk.data.local.entity.map

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "saved_places",
    foreignKeys = [
        ForeignKey(
            entity = FavoriteCategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE // 카테고리 삭제 시 내부 장소들도 자동 삭제
        )
    ],
    indices = [Index(value = ["categoryId", "placeId"], unique = true)]
)
data class SavedPlaceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val categoryId: Long,   // 소속된 카테고리 ID
    val placeId: String,    // Google Places API 고유 ID (사진 로딩 등에 사용)
    val name: String,       // 장소 이름
    val address: String,    // 주소
    val category: String,   // 장소 타입 (예: restaurant)
    val latitude: Double,   // 위도
    val longitude: Double   // 경도
)