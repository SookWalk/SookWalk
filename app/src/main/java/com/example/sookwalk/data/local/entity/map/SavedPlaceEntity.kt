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
    val remoteId: String = "",

    val categoryId: Long = 0,
    val placeId: String = "",
    val name: String = "",
    val address: String = "",
    val category: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)