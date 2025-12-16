package com.example.sookwalk.data.remote

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import kotlinx.coroutines.launch
import android.app.Service
import com.example.sookwalk.data.repository.StepRepository
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import java.time.LocalDate


@AndroidEntryPoint
class StepForegroundService : Service(), SensorEventListener {

    @Inject
    lateinit var repository: StepRepository

    private lateinit var sensorManager: SensorManager
    private var stepCounter: Sensor? = null

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private var lastCounterInMemory: Float? = null

    private var lastUploadedTodaySteps: Int = 0

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
        initSensor()

        serviceScope.launch {
            lastCounterInMemory = repository.getLastCounter()
        }
    }

    private fun initSensor() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        sensorManager.registerListener(
            this,
            stepCounter,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val current = event?.values?.get(0) ?: return

        serviceScope.launch {
            var last = lastCounterInMemory

            if (last == null) {
                lastCounterInMemory = current
                repository.saveLastCounter(current)
                return@launch
            }

            if (current < last) {
                lastCounterInMemory = current
                repository.saveLastCounter(current)
                return@launch
            }

            val diff = (current - last).toInt()
            if (diff <= 0) {
                return@launch
            }

            lastCounterInMemory = current
            repository.saveLastCounter(current)

            val todayAddedTotal = repository.addStepsForToday(diff)
            val totalSteps = repository.addToTotal(diff)

            if (todayAddedTotal - lastUploadedTodaySteps >= 100) {
                val today = LocalDate.now().toString()
                repository.uploadDailySteps(today, todayAddedTotal)
                repository.uploadTotalSteps(totalSteps)
                repository.addStepsToCollegeAndDepartment(todayAddedTotal)
                lastUploadedTodaySteps = todayAddedTotal
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) { }

    @SuppressLint("ForegroundServiceType")
    private fun startForegroundService() {
        val channelId = "step_channel"
        val channel = NotificationChannel(
            channelId,
            "Step Tracking",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        val notification = Notification.Builder(this, channelId)
            .setContentTitle("걸음 수 측정 중")
            .setContentText("걸음 수 기록이 실행 중입니다.")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        startForeground(1, notification)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        serviceScope.cancel()
    }
}



