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
import kotlinx.coroutines.runBlocking
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

            val todayStr = LocalDate.now().toString()
            lastUploadedTodaySteps = stepRepository.getStepsOfDate(todayStr)

            android.util.Log.d("StepService", "🚀 서비스 시작: 현재 $lastUploadedTodaySteps 보에서 시작")
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

            val isGoalJustCompleted = goalRepository.updateActiveGoalsProgressLocal(diff)
            if (isGoalJustCompleted) {
                android.util.Log.w("StepTrap", "🎯 [목표 달성 감지] 목표 완료로 인해 즉시 업로드 트리거됨!")
            }
            val currentTime = System.currentTimeMillis()
            val stepDiff = todayAddedTotal - lastUploadedTodaySteps
            val timeDiff = currentTime - lastUploadTime

            if (!isUploading && (stepDiff >= 50 || isGoalJustCompleted || (stepDiff > 0 && timeDiff >= 3 * 60 * 1000))) {
                isUploading = true
                try {
                    val oldLastSteps = lastUploadedTodaySteps // 혹시 몰라 백업 (필요시 롤백용이지만 지금은 그냥 둠)
                    lastUploadedTodaySteps = todayAddedTotal
                    lastUploadTime = currentTime

                    val todayStr = LocalDate.now().toString()

                    stepRepository.uploadDailySteps(todayStr, todayAddedTotal)
                    stepRepository.uploadTotalSteps(totalSteps)
                    stepRepository.updateStepStats(todayStr, totalSteps)
                    stepRepository.addStepsToCollegeAndDepartment(stepDiff)
                    goalRepository.syncActiveGoalsToFirebase()

                    android.util.Log.d("StepService", "☁️ 동기화 시도 완료")

                } catch (e: Exception) {
                    android.util.Log.e("StepService", "❌ 업로드 실패: ${e.message}")
                    e.printStackTrace()
                    // (선택) 실패했으니 기준점을 다시 되돌릴 수도 있지만,
                    // 무한 루프를 막기 위해 그냥 두는 게 낫습니다.
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

        runBlocking {
            try {
                val today = LocalDate.now().toString()
                val finalSteps = stepRepository.getStepsOfDate(today)
                val finalTotal = stepRepository.getTotalSteps()

                if (finalSteps > 0) {
                    stepRepository.uploadDailySteps(today, finalSteps)
                    stepRepository.uploadTotalSteps(finalTotal)
                    stepRepository.updateStepStats(today, finalTotal)
                    // 목표 상태 최종 저장
                    goalRepository.syncActiveGoalsToFirebase()

                    android.util.Log.d("FIREBASE_FINAL", "✅ 서비스 종료 전 최종 저장 완료")
                }
            } catch (e: Exception) {
                android.util.Log.e("FIREBASE_FINAL", "❌ 최종 저장 실패: ${e.message}")
            }
        }

        sensorManager.unregisterListener(this)
        serviceScope.cancel()
    }
}