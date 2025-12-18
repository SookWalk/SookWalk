package com.example.sookwalk.utils.notification.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.sookwalk.data.local.entity.notification.NotificationEntity
import com.example.sookwalk.data.repository.NotificationRepository
import com.example.sookwalk.data.repository.SettingsRepository
import com.example.sookwalk.utils.notification.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EverydayAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationRepository: NotificationRepository

    @Inject
    lateinit var settingsRepository: SettingsRepository // ★ 설정값 확인용

    override fun onReceive(context: Context, intent: Intent?) {
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val isNotiOn = settingsRepository.notificationFlow.first()

                // ★ 이 로그를 추가하세요
                Log.d("ALARM_CHECK", "알람 리시버 깨어남. 현재 설정값: $isNotiOn")

                if (!isNotiOn) {
                    // ★ 차단될 때 로그 남기기
                    Log.d("ALARM_CHECK", "사용자가 알림을 꺼둬서 알림 발송을 중단합니다. (PASS)")
                    return@launch
                }

                // ... 알림 발송 로직 ...
                Log.d("ALARM_CHECK", "알림 발송 완료! (SENT)")

            } finally {
                pendingResult.finish()
            }
        }
    }
}