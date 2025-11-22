package com.example.sookwalk.data.local.entity.notification

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val goalId: Int?,
    val title: String,
    val message: String,
    val createdAt: Long,
    val isRead: Boolean = false
)