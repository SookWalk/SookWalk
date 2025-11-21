package com.example.sookwalk.utils.notification.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.sookwalk.utils.notification.NotificationHelper

class EverydayAlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        NotificationHelper.showEverydayNotification(context)
    }
}