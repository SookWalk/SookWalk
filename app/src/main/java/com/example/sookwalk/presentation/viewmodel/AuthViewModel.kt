package com.example.sookwalk.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sookwalk.data.local.entity.user.UserEntity
import com.example.sookwalk.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor
    (private val repository: AuthRepository): ViewModel()
{
    var _isLoginIdAvailable = MutableStateFlow<Boolean>(false)
    val isLoginIdAvailable = _isLoginIdAvailable

    val currentUser: StateFlow<UserEntity?>
            = repository.currentUser.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000), // 5초간 구독이 없으면 중단
        initialValue = null )// 초기값 설정

    // 회원 가입
    fun insertNewAccount(user: UserEntity){
        viewModelScope.launch{
            repository.insertNewAccount(user)
        }
    }

    // 아이디 중복 여부 확인
    fun isLoginIdAvailable(loginId: String) {
        viewModelScope.launch {
            val available = repository.isLoginIdAvailable(loginId)
            _isLoginIdAvailable.value = available
        }
    }
}