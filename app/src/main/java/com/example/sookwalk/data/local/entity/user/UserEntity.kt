package com.example.sookwalk.data.local.entity.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,
    val major: String,
    val email: String,
    var nickname: String,
    var loginId: String,
    var password: String,
    var profileImageUrl: String
)