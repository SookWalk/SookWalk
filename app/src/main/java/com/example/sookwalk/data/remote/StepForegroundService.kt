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
            val totalSteps = stepRepository.addToTotal(diff)

            goalRepository.updateActiveGoalsProgress(diff)

            if (!isUploading && (todayAddedTotal - lastUploadedTodaySteps >= 100)) {
                isUploading = true // 업로드 시작 표시 (잠금)

                try {
                    // 마지막 업로드 기준점 미리 갱신 (중복 진입 방지)
                    lastUploadedTodaySteps = todayAddedTotal

                    // 파이어베이스 업로드 요청
                    stepRepository.uploadDailySteps(LocalDate.now().toString(), todayAddedTotal)
                    stepRepository.uploadTotalSteps(totalSteps)
                    stepRepository.addStepsToCollegeAndDepartment(todayAddedTotal)

                    android.util.Log.d("StepService", "✅ ${todayAddedTotal}보 업로드 완료")
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    isUploading = false // 업로드 끝남 표시 (잠금 해제)
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



