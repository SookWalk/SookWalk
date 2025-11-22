package com.example.sookwalk.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sookwalk.data.local.entity.user.UserEntity
import com.example.sookwalk.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private var _isAvailableNickname = MutableStateFlow<Boolean>(false)
    val isAvailableNickname = _isAvailableNickname


    val currentUser: StateFlow<UserEntity?>
            = userRepository.currentUser.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000), // 5초간 구독이 없으면 중단
        initialValue = null )// 초기값 설정

    // 닉네임 중복 체크
    fun isNicknameAvailable(nickname: String){
        viewModelScope.launch {
            val available = userRepository.isNicknameAvailable(nickname)
            _isAvailableNickname.value = available
        }
    }

    // 닉네임, 학과 변경
    fun updateNicknameAndMajor(nickname: String, major: String) {
        viewModelScope.launch {
            userRepository.updateNicknameAndMajor(nickname, major)
        }
    }

    // 이미지 변경
    fun updateProfileImageUrl(profileImageUrl: String) {
        viewModelScope.launch {
            userRepository.updateProfileImageUrl(profileImageUrl)
        }
    }

}