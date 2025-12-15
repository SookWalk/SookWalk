package com.example.sookwalk.presentation.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sookwalk.data.local.entity.map.FavoriteCategoryEntity
import com.example.sookwalk.data.local.entity.map.SavedPlaceEntity
import com.example.sookwalk.data.local.entity.map.SearchHistoryEntity
import com.example.sookwalk.data.repository.MapRepository
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: MapRepository
) : ViewModel() {

    val favoriteCategories = repository.allCategories.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val searchHistory = repository.searchHistory.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _selectedCategoryPlaces = MutableStateFlow<List<SavedPlaceEntity>>(emptyList())
    val selectedCategoryPlaces: StateFlow<List<SavedPlaceEntity>> = _selectedCategoryPlaces

    // 검색 결과 및 제안
    private val _searchResults = MutableStateFlow<List<SavedPlaceEntity>?>(null)
    val searchResults: StateFlow<List<SavedPlaceEntity>?> = _searchResults

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _searchSuggestions = MutableStateFlow<List<AutocompletePrediction>>(emptyList())
    val searchSuggestions: StateFlow<List<AutocompletePrediction>> = _searchSuggestions

    private var sessionToken: AutocompleteSessionToken? = null
    private var searchJob: Job? = null

    // 기능 구현
    fun selectCategory(categoryId: Long) {
        viewModelScope.launch {
            repository.getPlacesByCategory(categoryId).collect { _selectedCategoryPlaces.value = it }
        }
    }

    fun addCategory(name: String, color: Long) {
        viewModelScope.launch { repository.addCategory(name, color) }
    }

    fun deleteCategory(category: FavoriteCategoryEntity) {
        viewModelScope.launch { repository.deleteCategory(category) }
    }

    fun search(query: String) {
        viewModelScope.launch {
            repository.addSearchHistory(query)

            _isSearching.value = true
            _searchResults.value = null
            val results = repository.searchPlaces(query)
            _searchResults.value = results
            _isSearching.value = false
        }
    }

    fun deleteSearchHistory(history: SearchHistoryEntity) {
        viewModelScope.launch {
            repository.deleteSearchHistory(history)
        }
    }

    fun clearSearchResults() { _searchResults.value = null }

    // 자동완성 (Debounce 적용)
    fun onQueryChange(newQuery: String) {
        if (sessionToken == null) sessionToken = AutocompleteSessionToken.newInstance()
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300L)
            if (newQuery.isNotEmpty()) {
                _searchSuggestions.value = repository.getAutocomplete(newQuery, sessionToken)
            } else {
                _searchSuggestions.value = emptyList()
            }
        }
    }

    fun onSearchFinished() {
        sessionToken = null
        _searchSuggestions.value = emptyList()
    }

    suspend fun getPlacePhotos(placeId: String): List<Bitmap> {
        return repository.getPlacePhotos(placeId)
    }

    fun savePlaceToCategories(place: SavedPlaceEntity, categoryIds: List<Long>) {
        viewModelScope.launch {
            repository.savePlaceToCategories(place, categoryIds)
        }
    }
}