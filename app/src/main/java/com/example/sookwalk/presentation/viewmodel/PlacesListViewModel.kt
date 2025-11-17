package com.example.sookwalk.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sookwalk.data.repository.FavoriteRepository
import com.example.sookwalk.presentation.screens.map.PlaceUiModel
import com.google.android.libraries.places.api.model.Place
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class PlacesViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    // 1. [외부에서 받음] API로 받아온 장소 목록
    private val _places = MutableStateFlow<List<Place>>(emptyList())

    // 2. [DB에서 관찰] 즐겨찾기 된 장소의 ID '집합(Set)'
    //    List<FavoritePlaceEntity> -> Flow<Set<String>>
    private val _favoriteIds: Flow<Set<String>> = favoriteRepository.getFavorites()
        .map { favoriteList ->
            // DB 목록이 바뀔 때마다 Set<String>으로 변환
            favoriteList.map { it.placeId }.toSet()
        }

    // 3. [최종 UI 상태] 1번과 2번을 조합(combine)
    //    Composable은 이것 '하나'만 구독합니다.
    val uiState: StateFlow<List<PlaceUiModel>> = _places
        .combine(_favoriteIds) { places, favIds ->
            // API 목록(places)을 순회하며
            // 즐겨찾기 ID 목록(favIds)에 포함되는지 확인
            places.map { place ->
                PlaceUiModel(
                    place = place,
                    isFavorite = favIds.contains(place.id)
                )
            }
        }.stateIn( // ViewModel이 살아있는 동안 이 Flow를 유지
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    /**
     * [외부에서 호출] MapScreen이나 검색 화면에서 API 결과를 받으면 호출
     */
    fun setPlaces(list: List<Place>) {
        _places.value = list
    }

    /**
     * [UI에서 호출] 즐겨찾기 버튼 클릭 시
     */
    fun onFavoriteClick(placeId: String) {
        viewModelScope.launch {
            favoriteRepository.toggleFavorite(placeId)
        }
    }
}
