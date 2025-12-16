package com.example.sookwalk.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sookwalk.data.repository.BadgeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.google.firebase.Timestamp
import javax.inject.Inject

@HiltViewModel
class BadgeViewModel @Inject constructor(
    private val repository: BadgeRepository
): ViewModel() {

    private val _totalSteps = MutableStateFlow<Int>(0)
    val totalSteps: StateFlow<Int> = _totalSteps.asStateFlow()

    private val _stepLevel = MutableStateFlow<Int>(0)
    val stepLevel: StateFlow<Int> = _stepLevel.asStateFlow()

    private val _stepDate = MutableStateFlow<Timestamp?>(null)
    val stepDate: StateFlow<Timestamp?> = _stepDate.asStateFlow()

    private val _totalPlaces = MutableStateFlow<Int>(0)
    val totalPlaces: StateFlow<Int> = _totalPlaces.asStateFlow()

    private val _placeLevel = MutableStateFlow<Int>(0)
    val placeLevel: StateFlow<Int> = _placeLevel.asStateFlow()

    private val _placeDate = MutableStateFlow<Timestamp?>(null)
    val placeDate: StateFlow<Timestamp?> = _placeDate.asStateFlow()

    private val _totalRanks = MutableStateFlow<Int>(0)
    val totalRanks: StateFlow<Int> = _totalRanks.asStateFlow()

    private val _rankLevel = MutableStateFlow<Int>(0)
    val rankLevel: StateFlow<Int> = _rankLevel.asStateFlow()

    private val _rankDate = MutableStateFlow<Timestamp?>(null)
    val rankDate: StateFlow<Timestamp?> = _rankDate.asStateFlow()

    private val _totalChallenges = MutableStateFlow<Int>(0)
    val totalChallenges: StateFlow<Int> = _totalChallenges.asStateFlow()

    private val _challengeLevel = MutableStateFlow<Int>(0)
    val challengeLevel: StateFlow<Int> = _challengeLevel.asStateFlow()

    private val _challengeDate = MutableStateFlow<Timestamp?>(null)
    val challengeDate: StateFlow<Timestamp?> = _challengeDate.asStateFlow()


    // 총 걸음 수 가져오기
    fun getTotalSteps(){
        viewModelScope.launch {
            try {
                val (value, level, date) = repository.getStepsWithLevel()
                _totalSteps.value = value
                _stepLevel.value = level
                _stepDate.value = date
            } catch (e: Exception) {
                // 에러 처리
            }
        }
    }

    // 총 장소 수 가져오기
    fun getTotalPlaces(){
        viewModelScope.launch {
            try {
                val (value, level, date) = repository.getPlacesWithLevel()
                _totalPlaces.value = value
                _placeLevel.value = level
                _placeDate.value = date
            } catch (e: Exception) {
                // 에러 처리
            }
        }
    }

    // 총 랭크 수 가져오기
    fun getTotalRanks(){
        viewModelScope.launch {
            try {
                val (value, level, date) = repository.getRanksWithLevel()
                _totalRanks.value = value
                _rankLevel.value = level
                _rankDate.value = date
            } catch (e: Exception) {
                // 에러 처리
            }
        }
    }

    // 총 챌린지 수 가져오기
    fun getTotalChallenges(){
        viewModelScope.launch {
            try {
                val (value, level, date) = repository.getChallengesWithLevel()
                _totalChallenges.value = value
                _challengeLevel.value = level
                _challengeDate.value = date
            } catch (e: Exception) {
                // 에러 처리
            }
        }
    }

}