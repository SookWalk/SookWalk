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

    // DB
    val db = Firebase.firestore("sookwalk")

    // 로그인 시도
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn
    suspend fun login(loginId: String, password: String): Boolean {
        return try {
            val idInFirestore = db.collection("users")
                .whereEqualTo("loginId", loginId.trim()) // 공백 제거
                .get()
                .await()

            // 아이디가 존재하는지 먼저 확인
            if (idInFirestore.isEmpty) {
                false
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
        // FirebaseAuth로 계정 생성
        // FirebaseAuth에 저장할 땐 이메일 + 비밀번호로
        try {
            // 1. FirebaseAuth로 계정 생성하고 작업이 끝날 때까지 기다림
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            Log.d("SignUp", "FirebaseAuth 계정 생성 성공: ${authResult.user?.email}")

            val uid = authResult.user?.uid

            // 2. 계정 생성이 성공하면, Firestore에 정보 저장
            if (uid != null) {
                val user = UserEntity(
                    email = email,
                    loginId = loginId,
                    major = major,
                    nickname = nickname,
                    profileImageUrl = ""
                    // id = uid (아직 추가 X)
                )

                // Firestore에 사용자 UID를 문서 ID로 사용하여 정보 저장하고 기다림
                db.collection("users").document(uid)
                    .set(user)
                    .await()
                Log.d("SignUp", "Firestore에 회원정보 저장 성공")

                // 로컬 DB에 저장
                // Firestore까지 성공해야 로컬에도 저장
                dao.insert(user)
                Log.d("SignUp", "로컬 DB에 회원정보 저장 성공")

            } else {
                // 이 경우는 거의 없지만, 방어 코드로 추가
                throw IllegalStateException("FirebaseAuth 계정 생성 후 UID를 받지 못했습니다.")
            }

        } catch (e: Exception) {
            Log.e("SignUp", "회원가입 실패: ${e.message}")
            // 실패 사실을 호출한 쪽(ViewModel)에 알리기 위해 예외를 다시 던짐
            throw e
        }
    }

    // 아이디 중복 여부 확인
    suspend fun isLoginIdAvailable(loginId: String): Boolean {
        return try {
            Log.d("중복확인", "아이디 중복 확인 시도: '$loginId'")
            val result = db.collection("loginIds")
                .document(loginId.trim())
                //.whereEqualTo("loginId", loginId.trim()) // 공백 제거
                .get()
                .await()
            Log.d("중복확인", "아이디 중복 확인 시도2: '$loginId'")
            !result.exists() // 비어있으면 (중복 X 아이디면) 사용 가능
        }catch(e: Exception){
            Log.e("loginId", "아이디 중복 확인 실패", e)
            throw e
        }
    }
}