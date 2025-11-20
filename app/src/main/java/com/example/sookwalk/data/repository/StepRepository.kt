package com.example.sookwalk.data.repository

import android.content.Context
import com.example.sookwalk.data.local.StepCounterDataStore
import com.example.sookwalk.data.local.dao.StepDao
import com.example.sookwalk.data.local.entity.steps.DailyStepEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.time.LocalDate

@Singleton
class StepRepository @Inject constructor(
    private val stepDao: StepDao,
    @ApplicationContext private val context: Context
) {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // -------- Room --------

    suspend fun getStepsOfDate(date: String): Int {
        return stepDao.getSteps(date) ?: 0
    }

    // 오늘 걸음수를 delta 만큼 증가시키고, 증가 후 총 합을 리턴
    suspend fun addStepsForToday(delta: Int): Int {
        val today = LocalDate.now().toString()
        val current = stepDao.getSteps(today) ?: 0
        val newValue = current + delta
        stepDao.saveSteps(DailyStepEntity(today, newValue))
        return newValue
    }

    suspend fun getStepsBetween(start: String, end: String): Int {
        return stepDao.getStepsBetween(start, end)
    }

    // -------- DataStore (총 누적 / 마지막 센서값) --------

    suspend fun getLastCounter(): Float? =
        StepCounterDataStore.readLastCounter(context)

    suspend fun saveLastCounter(value: Float) =
        StepCounterDataStore.saveLastCounter(context, value)

    suspend fun getTotalSteps(): Int =
        StepCounterDataStore.readTotalSteps(context)

    suspend fun addToTotal(delta: Int): Int {
        val current = getTotalSteps()
        val newValue = current + delta
        StepCounterDataStore.saveTotalSteps(context, newValue)
        return newValue
    }

    // -------- Firebase --------

    private fun currentUserId(): String? = auth.currentUser?.uid

    suspend fun uploadDailySteps(date: String, steps: Int) {
        val uid = currentUserId() ?: return
        val data = mapOf("steps" to steps)
        firestore.collection("users")
            .document(uid)
            .collection("dailySteps")
            .document(date)
            .set(data, SetOptions.merge())
    }

    suspend fun uploadTotalSteps(total: Int) {
        val uid = currentUserId() ?: return
        val data = mapOf("totalSteps" to total)
        firestore.collection("users")
            .document(uid)
            .set(data, SetOptions.merge())
    }
}