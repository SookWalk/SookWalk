package com.example.sookwalk.presentation.viewmodel

import com.example.sookwalk.data.repository.NotificationRepository
import com.example.sookwalk.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
){


}