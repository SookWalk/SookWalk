package com.example.sookwalk.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sookwalk.data.local.entity.goal.GoalEntity
import com.example.sookwalk.data.repository.GoalRepository
import com.example.sookwalk.data.repository.SettingsRepository
import com.example.sookwalk.utils.notification.AlarmScheduler.cancelGoalNotification
import com.example.sookwalk.utils.notification.AlarmScheduler.scheduleGoalNotification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class GoalViewModel @Inject constructor(
    private val goalRepository: GoalRepository,
    private val settingsRepository: SettingsRepository
): ViewModel() {

    private val today: LocalDate
            = LocalDate.now()
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val weekStart: LocalDate = today.with(DayOfWeek.MONDAY)
    private val weekEnd: LocalDate = weekStart.plusDays(6)

    val todayGoals: Flow<GoalEntity?> =
        goalRepository.getGoalsByDate(today.format(formatter))
            .map {list -> list.firstOrNull()}
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = null
            )

    val weekGoals: Flow<List<GoalEntity>> =
        goalRepository.getGoalsOfWeek(weekStart.format(formatter), weekEnd.format(formatter))

    fun addGoal(context: Context, goal: GoalEntity) = viewModelScope.launch {
        val newId = goalRepository.insertGoal(goal).toInt()

        val enabled = settingsRepository.notificationFlow.first()
        if (enabled) {
            scheduleGoalNotification(context, newId, goal.title, goal.endDate)
        }
    }

    fun updateGoal(context: Context, goal: GoalEntity, memo: String) = viewModelScope.launch {
        goalRepository.updateGoalByMemo(goal.id, memo)
        scheduleGoalNotification(context, goal.id, goal.title, goal.endDate)
    }

    fun deleteGoal(context: Context, goal: GoalEntity) = viewModelScope.launch {
        goalRepository.deleteGoal(goal)
        cancelGoalNotification(context, goal.id)
    }

    fun getGoalsByDate(date: String): StateFlow<List<GoalEntity>> =
        goalRepository.getGoalsByDate(date).stateIn(
            viewModelScope, SharingStarted.Lazily, emptyList()
        )
}