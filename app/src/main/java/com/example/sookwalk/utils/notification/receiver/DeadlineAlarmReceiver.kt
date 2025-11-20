package com.example.sookwalk.utils.notification.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.sookwalk.data.local.entity.notification.NotificationEntity
import com.example.sookwalk.data.repository.NotificationRepository
import com.example.sookwalk.utils.notification.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DeadlineAlarmReceiver: BroadcastReceiver() {

    // 알람 히스토리에 저장하기 위함
    @Inject
    lateinit var notificationRepository: NotificationRepository

    override fun onReceive(context: Context, intent: Intent) {
        
        val title = intent?.getStringExtra("title")?: "목표 마감 알림"
        // 알람 클릭시 해당하는 goal로 가게끔 하기 위해 쓰이는 값
        val goalId = intent?.getIntExtra("goalId", -1)?: -1
        val label = intent?.getStringExtra("label")?: "조금"

        CoroutineScope(Dispatchers.IO).launch {
            val now = System.currentTimeMillis()
            val entity = NotificationEntity(
                title = title,
                goalId = goalId,
                message = label,
                createdAt = now,
            )
            notificationRepository.saveNotification(entity)
        }

        NotificationHelper.showDeadlineNotification(context, title, goalId, label)
    }
}
