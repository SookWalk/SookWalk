package com.example.sookwalk.data.repository

import com.example.sookwalk.data.local.dao.UserDao
import com.example.sookwalk.data.local.entity.user.UserEntity
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class UserRepository @Inject constructor(
    private val userDao: UserDao
){


    // 현재 사용중인 유저 정보 가져오기
    val currentUser: Flow<UserEntity?> = userDao.getCurrentUser()

    // 닉네임 중복 여부 확인
    suspend fun isNicknameAvailable(nickname: String): Boolean {
        val result = Firebase.firestore.collection("users")
            .whereEqualTo("nickname", nickname.trim()) // 공백 제거
            .get()
            .await()

        return result.isEmpty // 비어있으면 (중복 X 닉네임이면) 사용 가능
    }

    // 닉네임, 학과 변경
    suspend fun updateNicknameAndMajor(newNickname: String, newMajor: String) {
        userDao.updateNicknameAndMajor(newNickname, newMajor)
    }


    suspend fun updateProfileImageUrl(newProfileImageUrl: String) {
        userDao.updateProfileImageUrl(newProfileImageUrl)
    }

}
