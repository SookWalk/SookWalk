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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import jakarta.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class MapRepository @Inject constructor(
    private val favoriteDao: FavoriteDao,
    private val searchHistoryDao: SearchHistoryDao,
    private val placesClient: PlacesClient,
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    val allCategories: Flow<List<CategoryWithCount>> = favoriteDao.getAllCategories()
    val searchHistory: Flow<List<SearchHistoryEntity>> = searchHistoryDao.getSearchHistory()

    private fun userCol() = auth.currentUser?.uid?.let { uid ->
        db.collection("users").document(uid)
    }

    fun getPlacesByCategory(categoryId: Long) = favoriteDao.getPlacesByCategory(categoryId)

    suspend fun addCategory(name: String, color: Long) {
        val userRef = userCol()

        val newDocRef = userRef?.collection("favorite_categories")?.document()
        val generatedRemoteId = newDocRef?.id ?: ""

        val category = FavoriteCategoryEntity(
            name = name,
            iconColor = color,
            remoteId = generatedRemoteId
        )
        favoriteDao.insertCategory(category)

        if (newDocRef != null) {
            val firestoreData = mapOf(
                "remoteId" to generatedRemoteId,
                "name" to name,
                "iconColor" to color,
                "createdAt" to System.currentTimeMillis()
            )
            newDocRef.set(firestoreData)
        }
    }

    suspend fun deleteCategory(category: FavoriteCategoryEntity) {
        favoriteDao.deleteCategory(category)

        if (category.remoteId.isNotEmpty()) {
            try {
                val userRef = userCol() ?: return

                userRef.collection("favorite_categories").document(category.remoteId).delete()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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
        val userRef = userCol()

        categoryIds.forEach { localCatId ->
            val category = favoriteDao.getCategoryById(localCatId) ?: return@forEach

            var categoryRemoteId = category.remoteId

            if (categoryRemoteId.isEmpty()) {
                val newCatDoc = userRef?.collection("favorite_categories")?.document()
                val newId = newCatDoc?.id ?: ""

                if (newId.isNotEmpty()) {
                    val updatedCategory = category.copy(remoteId = newId)
                    favoriteDao.updateCategory(updatedCategory) // DAO에 update 함수 필요

                    val catData = mapOf(
                        "remoteId" to newId,
                        "name" to category.name,
                        "iconColor" to category.iconColor,
                        "createdAt" to System.currentTimeMillis()
                    )
                    newCatDoc?.set(catData)

                    categoryRemoteId = newId
                }
            }

            val newDocRef = userRef?.collection("saved_places")?.document()
            val placeRemoteId = newDocRef?.id ?: ""

            val newPlace = place.copy(
                id = 0,
                categoryId = localCatId,
                remoteId = placeRemoteId
            )
            favoriteDao.insertPlace(newPlace)

            if (placeRemoteId.isNotEmpty() && userRef != null) {
                val firestoreData = hashMapOf(
                    "remoteId" to placeRemoteId,
                    "categoryRemoteId" to categoryRemoteId, // ★ 이제 빈칸 안 들어감
                    "placeId" to place.placeId,
                    "name" to place.name,
                    "address" to place.address,
                    "category" to place.category,
                    "latitude" to place.latitude,
                    "longitude" to place.longitude
                )
                newDocRef?.set(firestoreData)
            }
        }
    }

    suspend fun syncFromFirebase() {
        val userRef = userCol() ?: return // 로그인 안 했으면 중단

        try {
            // ==========================================
            // 1단계: 카테고리 먼저 가져오기
            // ==========================================
            val catSnapshot = userRef.collection("favorite_categories").get().await()

            val remoteToLocalMap = mutableMapOf<String, Long>()

            catSnapshot.documents.forEach { doc ->
                val remoteId = doc.id // 문서 ID
                val name = doc.getString("name") ?: ""
                val color = doc.getLong("iconColor") ?: 0L

                val existingCat = favoriteDao.getCategoryByRemoteId(remoteId)

                val localId: Long = if (existingCat != null) {
                    // 이미 있으면: 업데이트 (선택사항) 하거나 기존 ID 사용
                    // dao.update(existingCat.copy(name = name, iconColor = color)) // 필요 시 주석 해제
                    existingCat.id
                } else {
                    // 없으면: 새로 삽입
                    val newCat = FavoriteCategoryEntity(
                        id = 0, // 0으로 넣으면 자동생성
                        name = name,
                        iconColor = color,
                        remoteId = remoteId
                    )
                    favoriteDao.insertCategory(newCat) // insert 후 생성된 ID 반환하도록 DAO 수정하면 좋음
                    favoriteDao.getCategoryByRemoteId(remoteId)?.id ?: 0L
                }

                if (localId != 0L) {
                    remoteToLocalMap[remoteId] = localId
                }
            }

            // ==========================================
            // 2단계: 장소 가져오기
            // ==========================================
            val placeSnapshot = userRef.collection("saved_places").get().await()

            placeSnapshot.documents.forEach { doc ->
                val remoteId = doc.id
                val placeData = doc.toObject(SavedPlaceEntity::class.java) ?: return@forEach

                val parentCategoryRemoteId = doc.getString("categoryRemoteId")

                val targetLocalCategoryId = remoteToLocalMap[parentCategoryRemoteId]

                if (targetLocalCategoryId != null) {
                    val existingPlace = favoriteDao.getPlaceByRemoteId(remoteId)

                    if (existingPlace == null) {
                        val newPlace = placeData.copy(
                            id = 0,
                            categoryId = targetLocalCategoryId, // ★ 여기서 연결됨
                            remoteId = remoteId
                        )
                        favoriteDao.insertPlace(newPlace)
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun addSearchHistory(query: String) {
        searchHistoryDao.insertHistory(SearchHistoryEntity(query = query))

    }

    suspend fun deleteSearchHistory(history: SearchHistoryEntity) {
        searchHistoryDao.deleteHistory(history)
    }
}