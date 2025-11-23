package com.example.sookwalk.data.repository

import android.util.Log
import com.example.sookwalk.data.local.dao.UserDao
import com.example.sookwalk.data.local.entity.user.UserEntity
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await


class AuthRepository @Inject constructor(
    private val dao: UserDao
) {

    // 현재 사용중인 유저 정보 가져오기
    val currentUser: Flow<UserEntity?> = dao.getCurrentUser()

    // 로그인 시도
    suspend fun login(loginId: String, password: String): Boolean {
        val idInFirestore = Firebase.firestore.collection("users")
            .whereEqualTo("loginId", loginId.trim()) // 공백 제거
            .get()
            .await()

        Log.d("login", "idInFirestore: $idInFirestore")

        // 아이디가 존재하는지 먼저 확인
        if(idInFirestore.isEmpty){
            return false
        }

        // 사용자가 있으면, 비밀번호 확인
        val userDocumnet = idInFirestore.documents.first()
        val pwInFirestore = userDocumnet.get("password") as String

        Log.d("login", "pwInFirestore: $pwInFirestore")

        // 비밀 번호 비교
        return pwInFirestore == password
    }

    // 회원 가입
    suspend fun insertNewAccount(user: UserEntity) {
        dao.insert(user)
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