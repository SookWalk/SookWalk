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
import android.util.Log
import androidx.room.Room
import com.example.sookwalk.data.local.StepDatabase
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

    // 메모리에 들고 있는 마지막 센서 값 (DataStore와 동기화)
    private var lastCounterInMemory: Float? = null

    // 파이어베이스에 마지막으로 업로드한 오늘 걸음수
    private var lastUploadedTodaySteps: Int = 0

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
        initSensor()

        // 앱 시작 시 DataStore에서 마지막 센서값 / 총 걸음수 로드
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

            // 첫 센서 값이면 baseline으로만 저장하고 리턴
            if (last == null) {
                lastCounterInMemory = current
                repository.saveLastCounter(current)
                return@launch
            }

            // 1) 재부팅 감지: 센서가 다시 0 근처로 돌아온 경우
            if (current < last) {
                // 리부팅으로 간주하고 baseline을 현재 값으로 리셋
                lastCounterInMemory = current
                repository.saveLastCounter(current)
                // 이 샘플은 기준 재설정만 하고, 이 이벤트에서는 걸음수 증가를 0으로 봄
                return@launch
            }

            // 2) 증가분 계산 (이번 센서 값 - 이전 센서 값)
            val diff = (current - last).toInt()
            if (diff <= 0) {
                // 증가 없으면 아무 것도 안 함
                return@launch
            }

            // baseline 업데이트
            lastCounterInMemory = current
            repository.saveLastCounter(current)

            // 3) 오늘 걸음수 / 전체 걸음수 누적
            val todayAddedTotal = repository.addStepsForToday(diff)
            val totalSteps = repository.addToTotal(diff)

            // 4) Firebase: 100보 단위로만 업로드
            if (todayAddedTotal - lastUploadedTodaySteps >= 100) {
                val today = LocalDate.now().toString()
                repository.uploadDailySteps(today, todayAddedTotal)
                repository.uploadTotalSteps(totalSteps)
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



