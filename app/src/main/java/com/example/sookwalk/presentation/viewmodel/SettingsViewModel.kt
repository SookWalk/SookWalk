package com.example.sookwalk.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sookwalk.data.repository.GoalRepository
import com.example.sookwalk.data.repository.SettingsRepository
import com.example.sookwalk.utils.notification.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val goalRepository: GoalRepository,
    @ApplicationContext private val context: Context
): ViewModel(){

    val darkMode = settingsRepository.darkModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val notification = settingsRepository.notificationFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    val location = settingsRepository.locationFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    fun toggleDarkMode(v: Boolean) = viewModelScope.launch { settingsRepository.setDarkMode(v) }
    fun toggleNotification(v: Boolean) = viewModelScope.launch {
        settingsRepository.setNotification(v)

        if (v){ // 알림 키는 경우
            AlarmScheduler.scheduleEveryday8AMAlarm(context)
            val goals = goalRepository.getAllGoalsOnce()
            goals.forEach { goal ->
                AlarmScheduler.scheduleGoalNotification(
                    context = context,
                    goalId = goal.id,
                    title = goal.title,
                    dueDate = goal.endDate
                )
            }
            // TODO: 목표 달성률 알림, 뱃지 알림
        } else { // 알람 끄는 경우
            AlarmScheduler.cancelEveryday8AMAlarm(context)
            val goals = goalRepository.getAllGoalsOnce()
            goals.forEach { goal ->
                AlarmScheduler.cancelGoalNotification(
                    context = context,
                    goalId = goal.id
                )
            }
            // TODO: 목표 달성률 알림, 뱃지 알림
        }
    }

    fun toggleLocation(v: Boolean) = viewModelScope.launch { settingsRepository.setLocation(v) }
}