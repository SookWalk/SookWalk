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
import com.example.sookwalk.data.repository.GoalRepository
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
    lateinit var stepRepository: StepRepository

    @Inject
    lateinit var goalRepository: GoalRepository

    private lateinit var sensorManager: SensorManager
    private var stepCounter: Sensor? = null

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private var lastCounterInMemory: Float? = null

    private var lastUploadedTodaySteps: Int = 0

    private var lastUploadTime: Long = 0L

    private var isUploading = false

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
        initSensor()

        serviceScope.launch {
            lastCounterInMemory = stepRepository.getLastCounter()
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
                stepRepository.saveLastCounter(current)
                return@launch
            }

            if (current < last) {
                lastCounterInMemory = current
                stepRepository.saveLastCounter(current)
                return@launch
            }

            val diff = (current - last).toInt()
            if (diff <= 0) {
                return@launch
            }

            lastCounterInMemory = current
            stepRepository.saveLastCounter(current)

            val todayAddedTotal = stepRepository.addStepsForToday(diff)
            goalRepository.updateActiveGoalsProgress(diff)

            val currentTime = System.currentTimeMillis()
            val stepDiff = todayAddedTotal - lastUploadedTodaySteps
            val timeDiff = currentTime - lastUploadTime

            if (!isUploading && (stepDiff >= 50 || (stepDiff > 0 && timeDiff >= 3 * 60 * 1000))) {
                isUploading = true
                try {
                    // 업로드 전 기준점 업데이트
                    val stepsToUpload = todayAddedTotal
                    val totalToUpload = stepRepository.getTotalSteps()

                    stepRepository.uploadDailySteps(LocalDate.now().toString(), stepsToUpload)
                    stepRepository.uploadTotalSteps(totalToUpload)

                    // 랭킹은 '누적된 차이값'을 보냄 (중요!)
                    stepRepository.addStepsToCollegeAndDepartment(stepDiff)

                    // 기준점 갱신
                    lastUploadedTodaySteps = stepsToUpload
                    lastUploadTime = currentTime

                    android.util.Log.d("StepService", "☁️ 최적화 동기화 완료: $stepsToUpload 보")
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    isUploading = false
                }
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

        serviceScope.launch {
            try {
                val today = LocalDate.now().toString()
                val finalSteps = stepRepository.getStepsOfDate(today) // 로컬의 최종 걸음 수
                val finalTotal = stepRepository.getTotalSteps() // 로컬의 최종 누적 걸음 수

                if (finalSteps > 0) {
                    stepRepository.uploadDailySteps(today, finalSteps)
                    stepRepository.uploadTotalSteps(finalTotal)
                    android.util.Log.d("FIREBASE_FINAL", "✅ 서비스 종료 전 최종 저장 완료: $finalSteps 보")
                }
            } catch (e: Exception) {
                android.util.Log.e("FIREBASE_FINAL", "❌ 최종 저장 실패: ${e.message}")
            }
        }

        sensorManager.unregisterListener(this)
        serviceScope.cancel()
    }
}



