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
        val idInFirestore = Firebase.firestore.collection("users")
            .whereEqualTo("loginId", loginId.trim()) // 공백 제거
            .get()
            .await()

        // 아이디가 존재하는지 먼저 확인
        if (idInFirestore.isEmpty) {
            return false
        }

        // 사용자가 있으면, 이메일을 받아온다
        val email = idInFirestore.documents.first().get("email") as String

        // 실제 로그인은 FirebaseAuth(email, password)
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isLoggedIn.value = task.isSuccessful
            }
            .addOnFailureListener {
                _isLoggedIn.value = false
            }
        if(_isLoggedIn.value){
            return true
        } else {
            return false
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

        val user: UserEntity = UserEntity(
            email = email,
            loginId = loginId,
            major = major,
            nickname = nickname,
            profileImageUrl = ""
        )

        // 로컬에 회원 정보 저장
        dao.insert(user)

        // FirebaseAuth로 계정 생성
        // FirebaseAuth에 저장할 땐 이메일 + 비밀번호로
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener{ Log.d("SignUp","가입 완료: ${it.user?.email}") }
            .addOnFailureListener{ Log.d("SignUp", "가입 실패: ${it.message}") }

        // Firestore에 정보 저장
        Firebase.firestore.collection("users")
            // 로컬에 저장한 같은 정보를 저장
            .add(user)
            .addOnSuccessListener {
                Log.d("login", "회원정보 저장 성공")
            }
            .addOnFailureListener {
                Log.d("login", "회원정보 저장 실패")
            }
    }

    // 아이디 중복 여부 확인
    suspend fun isLoginIdAvailable(loginId: String): Boolean {
        val result = Firebase.firestore.collection("users")
            .whereEqualTo("loginId", loginId.trim()) // 공백 제거
            .get()
            .await()

        return result.isEmpty // 비어있으면 (중복 X 아이디면) 사용 가능
    }
}