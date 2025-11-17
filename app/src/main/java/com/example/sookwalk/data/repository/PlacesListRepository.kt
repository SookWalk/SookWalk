package com.example.sookwalk.data.repository

import com.example.sookwalk.data.local.dao.FavoritePlaceDao
import com.example.sookwalk.data.local.entity.map.FavoritePlaceEntity
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class FavoriteRepository @Inject constructor(
    private val dao: FavoritePlaceDao
) {

    fun isFavorite(placeId: String) = dao.isFavorite(placeId)

    fun getFavorites() = dao.getAllFavorites()

    suspend fun toggleFavorite(placeId: String) {
        val isFavorite = dao.isFavorite(placeId).first()
        if (isFavorite) {
            dao.removeFavorite(FavoritePlaceEntity(placeId))
        } else {
            dao.addFavorite(FavoritePlaceEntity(placeId))
        }
    }
}
