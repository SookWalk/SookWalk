package com.example.sookwalk.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.sookwalk.data.local.entity.map.FavoritePlaceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritePlaceDao {

    @Query("SELECT * FROM favorite_place")
    fun getAllFavorites(): Flow<List<FavoritePlaceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(place: FavoritePlaceEntity)

    @Delete
    suspend fun removeFavorite(place: FavoritePlaceEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_place WHERE placeId = :placeId)")
    fun isFavorite(placeId: String): Flow<Boolean>
}
