package com.example.sookwalk.data.repository

import android.graphics.Bitmap
import com.example.sookwalk.data.local.dao.FavoriteDao
import com.example.sookwalk.data.local.dao.SearchHistoryDao
import com.example.sookwalk.data.local.entity.map.CategoryWithCount
import com.example.sookwalk.data.local.entity.map.FavoriteCategoryEntity
import com.example.sookwalk.data.local.entity.map.SavedPlaceEntity
import com.example.sookwalk.data.local.entity.map.SearchHistoryEntity
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchByTextRequest
import jakarta.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class MapRepository @Inject constructor(
    private val favoriteDao: FavoriteDao,
    private val searchHistoryDao: SearchHistoryDao,
    private val placesClient: PlacesClient
) {
    val allCategories: Flow<List<CategoryWithCount>> = favoriteDao.getAllCategories()
    val searchHistory: Flow<List<SearchHistoryEntity>> = searchHistoryDao.getSearchHistory()

    fun getPlacesByCategory(categoryId: Long) = favoriteDao.getPlacesByCategory(categoryId)

    suspend fun addCategory(name: String, color: Long) {
        favoriteDao.insertCategory(FavoriteCategoryEntity(name = name, iconColor = color))
    }

    suspend fun deleteCategory(category: FavoriteCategoryEntity) {
        favoriteDao.deleteCategory(category)
    }

    suspend fun searchPlaces(query: String): List<SavedPlaceEntity> {
        val placeFields = listOf(Place.Field.ID, Place.Field.DISPLAY_NAME, Place.Field.FORMATTED_ADDRESS, Place.Field.LOCATION, Place.Field.TYPES)
        val request = SearchByTextRequest.builder(query, placeFields).setMaxResultCount(10).build()

        return try {
            val response = placesClient.searchByText(request).await()
            response.places.map { place ->
                SavedPlaceEntity(
                    categoryId = -1,
                    placeId = place.id ?: "",
                    name = place.displayName ?: "이름 없음",
                    address = place.formattedAddress ?: "",
                    category = place.placeTypes?.firstOrNull() ?: "장소",
                    latitude = place.location?.latitude ?: 0.0,
                    longitude = place.location?.longitude ?: 0.0
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getAutocomplete(query: String, token: AutocompleteSessionToken?): List<AutocompletePrediction> {
        val request = FindAutocompletePredictionsRequest.builder()
            .setSessionToken(token)
            .setQuery(query)
            .setCountries("KR")
            .build()
        return try {
            placesClient.findAutocompletePredictions(request).await().autocompletePredictions
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getPlacePhotos(placeId: String): List<Bitmap> = coroutineScope {
        try {
            val fields = listOf(Place.Field.PHOTO_METADATAS)
            val placeResponse = placesClient.fetchPlace(FetchPlaceRequest.builder(placeId, fields).build()).await()
            val metadatas = placeResponse.place.photoMetadatas?.take(3) ?: return@coroutineScope emptyList()

            metadatas.map { metadata ->
                async {
                    try {
                        val request = FetchPhotoRequest.builder(metadata).setMaxWidth(500).setMaxHeight(500).build()
                        placesClient.fetchPhoto(request).await().bitmap
                    } catch (e: Exception) { null }
                }
            }.awaitAll().filterNotNull()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun savePlaceToCategories(place: SavedPlaceEntity, categoryIds: List<Long>) {
        categoryIds.forEach { catId ->
            val newPlace = place.copy(
                id = 0,
                categoryId = catId
            )
            favoriteDao.insertPlace(newPlace)
        }
    }

    suspend fun addSearchHistory(query: String) {
        searchHistoryDao.insertHistory(SearchHistoryEntity(query = query))

    }

    suspend fun deleteSearchHistory(history: SearchHistoryEntity) {
        searchHistoryDao.deleteHistory(history)
    }
}