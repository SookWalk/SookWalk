package com.example.sookwalk.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sookwalk.data.local.entity.notification.NotificationEntity
import com.example.sookwalk.data.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: NotificationRepository
): ViewModel(){
    val notificationList: StateFlow<List<NotificationEntity>> =
        repository.notifications.stateIn(
            viewModelScope, SharingStarted.Lazily, emptyList()
        )

    fun markAsRead(id: Int){
        viewModelScope.launch {
            repository.markAsRead(id)
        }
    }

    fun clearAll(){
        viewModelScope.launch{
            repository.clearAll()
        }
    }
}