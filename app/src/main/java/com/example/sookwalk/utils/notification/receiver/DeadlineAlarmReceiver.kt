package com.example.sookwalk.utils.notification.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.sookwalk.data.local.entity.notification.NotificationEntity
import com.example.sookwalk.data.repository.NotificationRepository
import com.example.sookwalk.data.repository.SettingsRepository
import com.example.sookwalk.utils.notification.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DeadlineAlarmReceiver: BroadcastReceiver() {

    // 알람 히스토리에 저장하기 위함
    @Inject
    lateinit var notificationRepository: NotificationRepository

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onReceive(context: Context, intent: Intent) {
        
        val title = intent?.getStringExtra("title")?: "목표 마감 알림"
        val goalId = intent?.getIntExtra("goalId", -1)?: -1
        val label = intent?.getStringExtra("label")?: "조금"

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val isNotiOn = settingsRepository.notificationFlow.first()
                if (!isNotiOn) {
                    return@launch
                }

                val now = System.currentTimeMillis()

                val fullMessage = "목표 달성까지 $label 남았어요!"

                val entity = NotificationEntity(
                    title = title,
                    goalId = goalId,
                    message = fullMessage,
                    createdAt = now,
                )
                notificationRepository.saveNotification(entity)

                NotificationHelper.showDeadlineNotification(context, title, goalId, label)

            } finally {
                pendingResult.finish()
            }
        }
    }
}
