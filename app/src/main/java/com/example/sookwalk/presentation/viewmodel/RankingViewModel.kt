package com.example.sookwalk.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.sookwalk.data.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RankingViewModel @Inject constructor(
    private val repository: NotificationRepository
): ViewModel(){

}