package com.example.sookwalk.utils.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.sookwalk.MainActivity
import com.example.sookwalk.R
import kotlin.jvm.java

// 알림을 화면에 띄워주는 Helper
object NotificationHelper {
    private const val CHANNEL_ID = "main_channel"
    private const val CHANNEL_NAME = "Main Notification"
    private const val NOTIFICATION_EVERYDAY_ID = 1001
    private const val NOTIFICATION_DEADLINE_ID = 1002
    private const val NOTIFICATION_ACHIEVE_ID = 1003

    // 알림 채널을 만드는 함수
    fun createNotificationChannel(context: Context){
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Main channel for notifications"
            }
        }
    }

    // 알림을 띄우는 함수
    // 정기적인 알람을 띄우는 함수
    fun showEverydayNotification(context: Context){
        // 여기서 Navigation으로 이동할 페이지 데이터를 포함해서 구성하기
        val intent = Intent(context, MainActivity::class.java).apply{
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra( "navigation","everydayNotification")
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val everydayBuilder = NotificationCompat.Builder(context,CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_walking_man)
            .setContentTitle("오늘도 가볍게 한 번 걸어볼까요?")
            .setContentText("아침 10분 산책이 하루를 바꿔줘요! 지금 잠깐 나가볼까요?")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED){
            NotificationManagerCompat.from(context).notify(NOTIFICATION_EVERYDAY_ID, everydayBuilder.build())
        }
    }
    
    // 마감 기한 알림을 띄우는 함수
    fun showDeadlineNotification(context: Context, title: String, goalId: Int, due: String) {
        // 여기서 Navigation으로 이동할 페이지 데이터를 포함해서 구성하기
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra( "navigation","deadline")
            putExtra( "goalId",goalId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context, goalId, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val deadlineBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_walking_man)
            .setContentTitle(title)
            .setContentText("목표까지 ${due} 남았어요! 오늘 조금만 더 해볼까요?")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context)
                .notify(NOTIFICATION_DEADLINE_ID, deadlineBuilder.build())
        }
    }

    // TODO: 목표 달성 알람을 띄우는 함수 - 걸음 수와 비교해서 달성 시
    fun showAchieveNotification(context: Context) {
        // 여기서 Navigation으로 이동할 페이지 데이터를 포함해서 구성하기
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val achieveBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_walking_man)
            .setContentTitle("목표 달성 축하드려요!")
            .setContentText("오늘도 멋진 성장을 이뤘어요. 꾸준함이 빛을 내고 있어요!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context)
                .notify(NOTIFICATION_EVERYDAY_ID, achieveBuilder.build())
        }
    }
}