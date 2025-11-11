package com.example.sookwalk.data.local.entity.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "members")
data class `UserEntity.kt`(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val email: String,
    val nickname: String,
    val loginId: String,
    val password: String,
    var isNotice: Boolean,
    var isTracked: Boolean,
    var isDark: Boolean,
    val phonNumber: String,

    val test: String
)
