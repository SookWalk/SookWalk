package com.example.sookwalk.utils.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import com.example.sookwalk.utils.notification.receiver.DeadlineAlarmReceiver
import com.example.sookwalk.utils.notification.receiver.EverydayAlarmReceiver
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar

// 알람 스케줄링 설정
object AlarmScheduler {
    private const val NOTIFICATION_EVERYDAY_REQ = 9876

    // 매일 아침 8시로 알람 스케줄 설정
    fun scheduleEveryday8AMAlarm(context: Context){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, EverydayAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, NOTIFICATION_EVERYDAY_REQ, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val calendar = Calendar.getInstance().apply{
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            
            // 이미 오늘 8시를 지나쳤는지 확인 후, 지나쳤을 경우 내일 8시로 미룬다
            if (before(Calendar.getInstance())){
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    // 매일 알람 끄기
    fun cancelEveryday8AMAlarm(context: Context){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, EverydayAlarmReceiver::class.java)
        val pendingIntent =  PendingIntent.getBroadcast(context, NOTIFICATION_EVERYDAY_REQ, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        alarmManager.cancel(pendingIntent)
    }

    // 마감 알림 예약
    fun scheduleGoalNotification(context: Context, goalId: Int, title: String, dueDate: String) {
        // 버전 체크
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            // 권한이 없으면 알람 스케줄링을 진행하지 않고 종료
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                context.startActivity(intent)
                return
            }
        }

        val dueDateTime = LocalDate.parse(dueDate)
            .atTime(23, 59)
        val now = LocalDateTime.now()
        val triggerDateTimes = listOf(
            dueDateTime.minusDays(7) to "D-7",
            dueDateTime.minusDays(5) to "D-5",
            dueDateTime.minusDays(1) to "D-1",
            dueDateTime.minusDays(0) to "D-Day"
        ) // 이미 지난 시간은 알람 등록을 하지 않는다(지나버리면 바로 울리기 때문)
            .filter { (t, _) -> t.isAfter(now) }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // AlarmManager가 이해할 수 있는 숫자로 변경
        triggerDateTimes.forEachIndexed { index, (triggerDateTime, label) ->
            val triggerMillis = triggerDateTime
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

            val requestCode = goalId * 100 + index

            // DeadlineAlarmReceiver로 보낼 Intent 생성
            val intent = Intent(context, DeadlineAlarmReceiver::class.java).apply {
                putExtra("title", title)
                putExtra("goalId", goalId)
                putExtra("label", label)
            }

            // PendingIntent 생성
            val pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            // AlarmManger로 실제 알람 등록
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerMillis,
                pendingIntent
            )
        }
    }

    // 마감 알림 예약 취소
    fun cancelGoalNotification(context: Context, goalId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        (0..3).forEach { index ->
            val requestCode = goalId * 100 + index
            val intent = Intent(context, DeadlineAlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            alarmManager.cancel(pendingIntent)
        }
    }
}