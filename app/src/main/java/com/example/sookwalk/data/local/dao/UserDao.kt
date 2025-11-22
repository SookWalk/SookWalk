package com.example.sookwalk.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.sookwalk.data.local.entity.user.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao{
    @Query("SELECT * FROM users LIMIT 1")
    fun getCurrentUser(): Flow<UserEntity?>

    @Insert
    suspend fun insert(user: UserEntity)

    @Delete
    suspend fun delete(user: UserEntity)

    @Update
    suspend fun update(user: UserEntity)

    @Query("UPDATE users SET nickname = :newNickname, major = :newMajor")
    suspend fun updateNicknameAndMajor(newNickname: String, newMajor: String)

    @Query("UPDATE users SET profileImageUrl = :newProfileImageUrl")
    suspend fun updateProfileImageUrl(newProfileImageUrl: String)

}