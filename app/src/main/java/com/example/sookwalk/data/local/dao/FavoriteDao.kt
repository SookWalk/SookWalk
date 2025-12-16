package com.example.sookwalk.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.sookwalk.data.local.entity.map.CategoryWithCount
import com.example.sookwalk.data.local.entity.map.FavoriteCategoryEntity
import com.example.sookwalk.data.local.entity.map.SavedPlaceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("""
        SELECT 
            favorite_categories.*, 
            COUNT(saved_places.id) as placeCount
        FROM favorite_categories
        LEFT JOIN saved_places ON favorite_categories.id = saved_places.categoryId
        GROUP BY favorite_categories.id
        ORDER BY favorite_categories.createdAt DESC
    """)
    fun getAllCategories(): Flow<List<CategoryWithCount>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: FavoriteCategoryEntity): Long

    @Delete
    suspend fun deleteCategory(category: FavoriteCategoryEntity)

    @Query("SELECT * FROM saved_places WHERE categoryId = :catId")
    fun getPlacesByCategory(catId: Long): Flow<List<SavedPlaceEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlace(place: SavedPlaceEntity): Long

    @Query("SELECT * FROM favorite_categories WHERE id = :id")
    suspend fun getCategoryById(id: Long): FavoriteCategoryEntity?

    @Query("SELECT * FROM favorite_categories WHERE remoteId = :remoteId LIMIT 1")
    suspend fun getCategoryByRemoteId(remoteId: String): FavoriteCategoryEntity?

    @Query("SELECT * FROM saved_places WHERE remoteId = :remoteId LIMIT 1")
    suspend fun getPlaceByRemoteId(remoteId: String): SavedPlaceEntity?

    @Update
    suspend fun updateCategory(category: FavoriteCategoryEntity)
}