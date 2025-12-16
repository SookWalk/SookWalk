package com.example.sookwalk.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sookwalk.data.repository.StepRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

@HiltViewModel
class StepViewModel @Inject constructor(
    private val repo: StepRepository
) : ViewModel() {

    private val _todaySteps = MutableStateFlow(0)
    val todaySteps: StateFlow<Int> = repo.getStepsFlow(LocalDate.now().toString())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _totalSteps = MutableStateFlow(0)
    val totalSteps: StateFlow<Int> = _totalSteps

    private val _challengeSteps = MutableStateFlow(0)
    val challengeSteps: StateFlow<Int> = _challengeSteps

    private val auth = FirebaseAuth.getInstance()

    init {
        auth.addAuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
                viewModelScope.launch {
                    // 1. 파이어베이스 -> 로컬 DB 복구
                    repo.syncStepsFromFirebase()

                    // 2. 복구된 데이터로 화면 갱신
                    loadTodaySteps()
                    loadTotalSteps()
                }
            }
        }
    }

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

    suspend fun getStepsForDate(date: String): Int {
        return repo.getStepsOfDate(date)
    }
}


