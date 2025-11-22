package com.example.sookwalk.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
        // 화면 간 이동 시에도 저장되어야하는 정보
        var loginId by mutableStateOf("")
            private set
        var password by mutableStateOf("")
            private set
        var nickname by mutableStateOf("")
            private set
        var major by mutableStateOf("")
            private set
        var email by mutableStateOf("")
        private set


    // 각 정보에 대한 세터
    fun updateLoginId(loginId: String){
        this.loginId = loginId
    }

    fun updatePassword(password: String){
        this.password = password
    }

    fun updateNickname(nickname: String){
        this.nickname = nickname
    }

    fun updateMajor(major: String){
        this.major = major
    }

    fun updateEmail(eamil: String){
        this.email = email
    }

    // 현재 유저 정보 가져오기
    val currentUser: StateFlow<UserEntity?>
            = repository.currentUser.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000), // 5초간 구독이 없으면 중단
        initialValue = null )// 초기값 설정

    // 로그인

    // 로그인 가능 여부 저장
    var _isLoginSuccess = MutableStateFlow<Boolean>(false)
    val isLoginSuccess = _isLoginSuccess
    fun login(loginId: String, password: String){
        viewModelScope.launch {
            var _isLoginSuccess = repository.login(loginId, password)
            isLoginSuccess.value = _isLoginSuccess
        }
    }


    // 회원 가입
    fun insertNewAccount(user: UserEntity){
        viewModelScope.launch{
            repository.insertNewAccount(user)
        }
    }


    // 아이디 중복 여부 확인

    // 닉네임 사용 가능 여부 저장
    var _isLoginIdAvailable = MutableStateFlow<Boolean>(true)
    val isLoginIdAvailable = _isLoginIdAvailable

    fun isLoginIdAvailable(loginId: String){
        viewModelScope.launch {
            val available = repository.isLoginIdAvailable(loginId)
            _isLoginIdAvailable.value = available
        }
    }
}