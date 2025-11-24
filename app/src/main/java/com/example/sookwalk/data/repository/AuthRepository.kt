package com.example.sookwalk.data.repository

import android.util.Log
import com.example.sookwalk.data.local.dao.UserDao
import com.example.sookwalk.data.local.entity.user.UserEntity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await


class AuthRepository @Inject constructor(
    private val dao: UserDao
) {

    // 현재 사용중인 유저 정보 가져오기
    val currentUser: Flow<UserEntity?> = dao.getCurrentUser()

    // FirebaseAuth로 로그인
    val auth = FirebaseAuth.getInstance()

    // 로그인 시도
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn
    suspend fun login(loginId: String, password: String): Boolean {
        return try {
            val idInFirestore = Firebase.firestore.collection("users")
                .whereEqualTo("loginId", loginId.trim()) // 공백 제거
                .get()
                .await()

            // 아이디가 존재하는지 먼저 확인
            if (idInFirestore.isEmpty) {
                return false
            } else {
                val email = idInFirestore.documents.first().getString("email") ?: ""
                // FirebaseAuth로 로그인 시도, 성공하면 user 객체 반환, 실패하면 예외 발생
                auth.signInWithEmailAndPassword(email, password).await()
                _isLoggedIn.value = true
                true
            }
        } catch (e: Exception) {
            Log.e("LoginFailure", "로그인 실패: ${e.message}")
            false // 어떤 종류의 예외든 실패로 간주
        }
    }

    // 로그인 여부 확인 (익명 계정 제외)
    suspend fun isLoggedIn(): Boolean {
        val user = auth.currentUser
        return user != null && !user.isAnonymous
    }

    // 회원 가입
    suspend fun insertNewAccount(
        email: String,
        loginId: String,
        password: String,
        major: String,
        nickname: String
    ) {
        val user = UserEntity(
            email = email,
            loginId = loginId,
            major = major,
            nickname = nickname,
            profileImageUrl = ""
        )

        try {
            // 1. Firebase Auth 계정 생성 (await를 써서 다 될 때까지 기다림)
            auth.createUserWithEmailAndPassword(email, password).await()
            Log.d("SignUp", "Firebase Auth 계정 생성 성공")

            // 2. Firestore에 정보 저장 (계정 생성이 성공했을 때만 실행됨)
            Firebase.firestore.collection("users")
                .add(user)
                .await() // 저장 완료될 때까지 대기
            Log.d("SignUp", "Firestore 저장 성공")

            // 3. 로컬 DB 저장 (마지막에 저장)
            dao.insert(user)
            Log.d("SignUp", "Room DB 저장 성공")

        } catch (e: Exception) {
            // 회원가입 실패 시 로그 출력
            Log.e("SignUp", "회원가입 실패: ${e.message}")
            throw e // 뷰모델로 에러를 던져서 UI에서 알림을 띄우게 할 수도 있음
        }
    }

    // 아이디 중복 여부 확인
    suspend fun isLoginIdAvailable(loginId: String): Boolean {        // try-catch 구문으로 네트워크 및 권한 오류로부터 앱을 보호
        return try {
            val result = Firebase.firestore.collection("users")
                .whereEqualTo("loginId", loginId.trim())
                .get()
                .await()

            // 문서를 찾지 못하면 true(사용 가능) 반환
            result.isEmpty
        } catch (e: Exception) {
            // Firestore 접근에 실패하면 (네트워크, 권한 문제 등)
            // 안전을 위해 '사용 불가(false)'로 처리하고 에러 로그를 남긴다.
            Log.e("IdCheck", "아이디 중복 체크 실패: ${e.message}")
            false
        }
    }
}