package com.example.sookwalk

import android.app.Application
import com.example.sookwalk.data.local.entity.notification.NotificationSampleData
import com.example.sookwalk.data.repository.NotificationRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MyApp: Application(){
}