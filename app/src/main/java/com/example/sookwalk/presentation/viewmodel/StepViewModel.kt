package com.example.sookwalk.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sookwalk.data.repository.StepRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

@HiltViewModel
class StepViewModel @Inject constructor(
    private val repo: StepRepository
) : ViewModel() {

    private val _todaySteps = MutableStateFlow(0)
    val todaySteps: StateFlow<Int> = _todaySteps

    private val _totalSteps = MutableStateFlow(0)
    val totalSteps: StateFlow<Int> = _totalSteps

    private val _challengeSteps = MutableStateFlow(0)
    val challengeSteps: StateFlow<Int> = _challengeSteps

    fun loadTodaySteps() {
        viewModelScope.launch {
            val today = LocalDate.now().toString()
            _todaySteps.value = repo.getStepsOfDate(today)
        }
    }

    fun loadTotalSteps() {
        viewModelScope.launch {
            _totalSteps.value = repo.getTotalSteps()
        }
    }

    fun loadChallengeSteps(start: String, end: String) {
        viewModelScope.launch {
            _challengeSteps.value = repo.getStepsBetween(start, end)
        }
    }
}


