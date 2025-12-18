package com.example.sookwalk.data.repository

import android.content.Context
import com.example.sookwalk.data.enums.College
import com.example.sookwalk.data.enums.Department
import com.example.sookwalk.data.local.StepCounterDataStore
import com.example.sookwalk.data.local.dao.StepDao
import com.example.sookwalk.data.local.entity.steps.DailyStepEntity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class StepRepository @Inject constructor(
    private val stepDao: StepDao,
    private val db: FirebaseFirestore,
    @ApplicationContext private val context: Context
) {
    private val auth = FirebaseAuth.getInstance()
    private fun deptDoc(deptId: String)
            = db.collection("rankings").document("current").collection("departments").document(deptId)
    private fun collegeDoc(collegeId: String)
            = db.collection("rankings").document("current").collection("colleges").document(collegeId)

    fun getStepsFlow(date: String): Flow<Int> {
        return stepDao.getStepsFlow(date).map { it ?: 0 }
    }

    // Room
    suspend fun getStepsOfDate(date: String): Int {
        return stepDao.getSteps(date) ?: 0
    }

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

    // DataStore (총 누적 / 마지막 센서값)
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

    // Firebase
    private fun currentUserId(): String? = auth.currentUser?.uid

    suspend fun uploadDailySteps(date: String, steps: Int) {
        val uid = currentUserId() ?: return
        val data = mapOf("steps" to steps)
        db.collection("users")
            .document(uid)
            .collection("dailySteps")
            .document(date)
            .set(data, SetOptions.merge())
    }

    suspend fun uploadTotalSteps(total: Int) {
        val uid = currentUserId() ?: return
        val data = mapOf("totalSteps" to total)
        db.collection("users")
            .document(uid)
            .set(data, SetOptions.merge())
    }

    suspend fun addStepsToCollegeAndDepartment(delta: Int){
        val uid = currentUserId() ?: return
        val userRef = db.collection("users").document(uid)

        try {
            db.runTransaction { transaction ->
                val now = Timestamp.now()
                val userSnap = transaction.get(userRef)

                // [수정] throw 대신 return@runTransaction 사용
                // 정보가 없으면 에러를 내는 게 아니라, 그냥 이 트랜잭션만 종료합니다.
                val deptId = userSnap.getString("deptId") ?: return@runTransaction
                val collegeId = userSnap.getString("collegeId") ?: return@runTransaction

                val department = Department.fromId(deptId)
                val college = College.fromId(collegeId)

                val deptRef = deptDoc(department.id)
                val collegeRef = collegeDoc(college.id)

                transaction.update(
                    deptRef, mapOf(
                        "totalSteps" to FieldValue.increment(delta.toLong()),
                        "updatedAt" to now
                    )
                )

                transaction.update(
                    collegeRef, mapOf(
                        "totalSteps" to FieldValue.increment(delta.toLong()),
                        "updatedAt" to now
                    )
                )
            }.await()
        } catch (e: Exception) {
            // 랭킹 업데이트 실패가 전체 걸음 수 저장이나 목표 동기화를 방해하지 않도록 로그만 남김
            android.util.Log.e("StepRepo", "⚠️ 랭킹 업데이트 건너뜀: ${e.message}")
        }
    }

    suspend fun syncStepsFromFirebase() {
        val uid = auth.currentUser?.uid ?: return

        try {
            // 1. 일별 걸음 수(Daily Steps) 복구
            val snapshot = db.collection("users")
                .document(uid)
                .collection("dailySteps")
                .get()
                .await()

            if (!snapshot.isEmpty) {
                snapshot.documents.forEach { doc ->
                    val date = doc.id // 문서 ID가 날짜 (yyyy-MM-dd)
                    val remoteSteps = doc.getLong("steps")?.toInt() ?: 0

                    val localSteps = stepDao.getSteps(date) ?: 0

                    val finalSteps = maxOf(localSteps, remoteSteps)

                    stepDao.saveSteps(DailyStepEntity(date, finalSteps))
                }
            }

            // 2. 총 누적 걸음 수(Total Steps) 복구
            val userDoc = db.collection("users").document(uid).get().await()
            val remoteTotal = userDoc.getLong("totalSteps")?.toInt() ?: 0

            val localTotal = StepCounterDataStore.readTotalSteps(context)
            val finalTotal = maxOf(localTotal, remoteTotal)

            StepCounterDataStore.saveTotalSteps(context, finalTotal)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun updateStepStats(date: String, total: Int) {
        val uid = auth.currentUser?.uid ?: return
        try {
            val data = mapOf(
                "total" to total,
                "date" to FieldValue.serverTimestamp()
            )

            db.collection("users")
                .document(uid)
                .collection("stats")
                .document("step")
                .set(data, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            android.util.Log.e("StepRepo", "stats 업데이트 실패: ${e.message}")
        }
    }
}