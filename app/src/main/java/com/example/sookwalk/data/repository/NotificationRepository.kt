package com.example.sookwalk.data.repository

import com.example.sookwalk.data.local.dao.NotificationDao
import com.example.sookwalk.data.local.entity.notification.NotificationEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotificationRepository @Inject constructor(
    private val dao: NotificationDao
) {
    val notifications: Flow<List<NotificationEntity>> = dao.getAll()

    fun observeNotifications(): Flow<List<NotificationEntity>> =
        dao.getAll()

    suspend fun preloadIfEmpty(samples: List<NotificationEntity>) {
        if (dao.count() == 0) {
            samples.forEach { dao.insert(it) } // or insertAll 만들기
        }
    }

    suspend fun saveNotification(notification: NotificationEntity): Long {
        return dao.insert(notification)
    }

    suspend fun markAsRead(notificationId: Int) = dao.markAsRead(notificationId)

    suspend fun clearAll() = dao.clearAll()
}