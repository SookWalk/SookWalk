package com.example.sookwalk.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sookwalk.data.local.entity.user.UserEntity
import com.example.sookwalk.data.repository.AuthRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor
    (private val repository: AuthRepository): ViewModel() {

    // 화면 간 이동 시에도 저장되어야하는 정보
    private val _loginId = MutableStateFlow("")
    val loginId: StateFlow<String> = _loginId.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()



    // 각 정보에 대한 세터
    fun updateLoginId(newLoginId: String) {
        _loginId.value = newLoginId // 내부 MutableStateFlow 값 업데이트
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword // 내부 MutableStateFlow 값 업데이트
    }

    fun updateEmail(newEmail: String) {
        _email.value = newEmail // 내부 MutableStateFlow 값 업데이트
    }


    // 로그인

    // 로그인 여부 검사
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn

    fun checkLoginStatus() {
        viewModelScope.launch {
            _isLoggedIn.value = repository.isLoggedIn()
        }
    }

    // 로그인
    var _isLoginSuccess = MutableStateFlow<Boolean?>(null)
    val isLoginSuccess = _isLoginSuccess.asStateFlow()
    fun login(loginId: String, password: String) {
        viewModelScope.launch {
            var success = repository.login(loginId, password)
            _isLoginSuccess.value = success
        }
    }


    // 회원 가입
    fun signUp(
        email: String,
        loginId: String,
        password: String,
        nickname: String,
        major: String
    ) {
        viewModelScope.launch {
            repository.signUp(
                email = email,
                password = password,
                nickname = nickname,
                major = major,
                loginId = loginId,
                uid = ""
            )
        }
    }


    // 아이디 중복 여부 확인
    // 아이디 사용 가능 여부 저장
    var _isLoginIdAvailable = MutableStateFlow<Boolean?>(null)
    val isLoginIdAvailable = _isLoginIdAvailable.asStateFlow()

    fun isLoginIdAvailable(loginId: String) {
        viewModelScope.launch {
            try {
                val available = repository.isLoginIdAvailable(loginId)
                _isLoginIdAvailable.value = available
            } catch (e: Exception) {
                Log.e("AuthViewModel", "아이디 중복 확인 실패", e)
                // 예외 발생 시, 사용 불가능한 것으로 처리하거나
                // 별도의 에러 상태로 관리
                _isLoginIdAvailable.value = false
            }
        }
    }

    // 이메일 중복 여부 확인
    // 이메일 사용 가능 여부 저장
    var _isEmailAvailable = MutableStateFlow<Boolean?>(null)
    val isEmailAvailable = _isEmailAvailable.asStateFlow()

    fun isEmailAvailable(email: String) {
        viewModelScope.launch {
            try {
                val available = repository.isEmailAvailable(email)
                _isEmailAvailable.value = available
            } catch (e: Exception) {
                Log.e("AuthViewModel", "이메일 중복 확인 실패", e)
                // 예외 발생 시, 사용 불가능한 것으로 처리하거나
                // 별도의 에러 상태로 관리
                _isEmailAvailable.value = false
            }
        }
    }

    // 이메일 사용가능 여부 리셋
    fun resetEmailAvailable() {
        _isEmailAvailable.value = null
    }

}