package com.example.sookwalk.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.sookwalk.data.local.entity.notification.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Insert
    suspend fun insert(notification: NotificationEntity): Long

    @Query("SELECT * FROM notifications ORDER BY createdAt DESC")
    fun getAll(): Flow<List<NotificationEntity>>

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id:Int)

    @Query("DELETE FROM notifications")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM notifications")
    suspend fun count(): Int
}