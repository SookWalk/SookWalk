package com.example.sookwalk.data.repository

import com.example.sookwalk.data.local.dao.UserDao
import jakarta.inject.Inject

class UserRepository @Inject constructor(
    private val userDao: UserDao
){

    // 마이페이지 정보 수정 (닉네임, 학과)
    suspend fun updateNickNameMajor(nickname: String, major: String){
        userDao.updateNicknameMajor(nickname, major)
    }


}
