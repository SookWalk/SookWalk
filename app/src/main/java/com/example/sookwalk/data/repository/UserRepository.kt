package com.example.sookwalk.data.repository

import android.util.Log
import com.example.sookwalk.data.local.dao.UserDao
import com.example.sookwalk.data.local.entity.user.UserEntity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class UserRepository @Inject constructor(
    private val userDao: UserDao
) {

    val db = Firebase.firestore("sookwalk")

    // 현재 사용중인 유저 정보 가져오기
    val currentUser: Flow<UserEntity?> = userDao.getCurrentUser()

    // Firebase에서 현재 유저의 uid를 가져온다
    private val uid: String
        get() = Firebase.auth.currentUser?.uid
            ?: throw IllegalStateException("로그인되지 않은 상태에서 UserRepository 접근")


    // 닉네임 중복 여부 확인
    suspend fun isNicknameAvailable(nickname: String): Boolean {
        return try {
            Log.d("중복확인", "닉네임 중복 확인 시도: '$nickname'")
            val result = db.collection("nicknames")
                .document(nickname) // 문서 이름으로 비교
                .get()
                .await()
            Log.d("중복확인", "닉네임 중복 확인 시도2: '$nickname'")
            !result.exists() // 비어있으면 (중복 X 아이디면) 사용 가능
        }catch(e: Exception){
            Log.e("nickname", "닉네임 중복 확인 실패", e)
            throw e
        }
    }

    // 닉네임, 학과 변경
    suspend fun updateNicknameAndMajor(newNickname: String, newMajor: String) {

        userDao.updateNicknameAndMajor(newNickname, newMajor)

        if (newNickname != "") {
            // 기존 닉네임
            val oldNickname = db.collection("users").document(uid ?: "")
                .get()
                .await()
                .getString("nickname")

            // 기존 닉네임 삭제
            db.collection("nicknames")
                .document("$oldNickname")
                .delete()

            // 새로운 닉네임 추가
            db.collection("nicknames").document(newNickname)
                .set(mapOf("nickname" to newNickname))

            db.collection("users").document(uid).update(
                mapOf(
                    "nickname" to newNickname
                )
            ).await()
        }

        if (newMajor != "") {
            db.collection("users").document(uid).update(mapOf(
                "major" to newMajor )
            ).await()
        }

    }


    suspend fun updateProfileImageUrl(newProfileImageUrl: String) {

        userDao.updateProfileImageUrl(newProfileImageUrl)

        db.collection("users").document(uid).update(mapOf(
            "profileImageUrl" to newProfileImageUrl)
        ).await()
    }

}
