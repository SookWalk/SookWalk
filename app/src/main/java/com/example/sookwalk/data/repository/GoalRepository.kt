package com.example.sookwalk.data.repository

import android.content.Context
import android.util.Log
import com.example.sookwalk.data.local.dao.GoalDao
import com.example.sookwalk.data.local.entity.goal.GoalEntity
import com.example.sookwalk.utils.notification.NotificationHelper.showAchieveNotification
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GoalRepository @Inject constructor(
    private val dao: GoalDao,
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    @ApplicationContext private val context: Context // 알림용 컨텍스트
){
    private fun col(uid: String) =
        db.collection("users").document(uid).collection("goals")

    private val notifiedGoalIds = mutableSetOf<Int>()

    suspend fun insertGoal(uid: String, goal: GoalEntity): Long {
        val newDocRef = col(uid).document()
        val remoteId = newDocRef.id

        val goalWithRemote = goal.copy(remoteId = remoteId)
        val localId = dao.insert(goalWithRemote)

        try {
            val goalMap = hashMapOf(
                "remoteId" to remoteId,
                "title" to goal.title,
                "targetSteps" to goal.targetSteps,
                "currentSteps" to goal.currentSteps,
                "startDate" to goal.startDate,
                "endDate" to goal.endDate,
                "memo" to goal.memo,
                "isDone" to goal.isDone,
                "updatedAt" to Timestamp.now()
            )
            newDocRef.set(goalMap).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return localId
    }

    suspend fun deleteGoal(uid: String, goal: GoalEntity){
        dao.delete(goal)
        if (goal.remoteId.isNotEmpty()) {
            try {
                col(uid).document(goal.remoteId).delete().await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun syncGoalsFromFirebase(uid: String) {
        try {
            val snapshot = col(uid).get().await()

            if (!snapshot.isEmpty) {
                snapshot.documents.forEach { doc ->
                    val remoteId = doc.getString("remoteId") ?: doc.id
                    val title = doc.getString("title") ?: ""
                    val targetSteps = doc.getLong("targetSteps")?.toInt() ?: 0
                    val currentSteps = doc.getLong("currentSteps")?.toInt() ?: 0
                    val startDate = doc.getString("startDate") ?: ""
                    val endDate = doc.getString("endDate") ?: ""
                    val memo = doc.getString("memo") ?: ""
                    val isDone = doc.getBoolean("isDone") ?: false

                    if (title.isEmpty()) return@forEach

                    val remoteGoal = GoalEntity(
                        remoteId = remoteId,
                        title = title,
                        targetSteps = targetSteps,
                        currentSteps = currentSteps,
                        startDate = startDate,
                        endDate = endDate,
                        memo = memo,
                        isDone = isDone
                    )

                    val localGoal = dao.getGoalByRemoteId(remoteGoal.remoteId)

                    val finalGoal = if (localGoal != null) {
                        val mergedSteps = maxOf(localGoal.currentSteps, remoteGoal.currentSteps)
                        val mergedIsDone = localGoal.isDone || remoteGoal.isDone
                        remoteGoal.copy(
                            currentSteps = mergedSteps,
                            isDone = mergedIsDone,
                            id = localGoal.id
                        )
                    } else {
                        remoteGoal.copy(id = 0)
                    }

                    dao.insert(finalGoal)
                }
            }
        } catch (e: Exception) {
            Log.e("GoalRepository", "Goals sync failed: ${e.message}")
        }
    }

    suspend fun updateGoal(goal: GoalEntity) = dao.update(goal)

    fun getGoalsByDate(date: String): Flow<List<GoalEntity>> = dao.getGoalsByDate(date)

    fun getGoalsOfWeek(weekStart: String, weekEnd: String): Flow<List<GoalEntity>> =
        dao.getGoalsOfWeek(weekStart, weekEnd)

    suspend fun getAllGoalsOnce(): List<GoalEntity> {
        return dao.getAllGoalsOnce()
    }

    suspend fun updateGoalByMemo(uid: String, goal: GoalEntity, memo: String) {
        dao.updateMemo(goal.id, memo)
        if (goal.remoteId.isNotEmpty()) {
            try {
                val updates = mapOf(
                    "memo" to memo,
                    "updatedAt" to Timestamp.now()
                )
                col(uid).document(goal.remoteId).update(updates).await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun updateActiveGoalsProgressLocal(stepsDelta: Int): Boolean {
        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val uid = auth.currentUser?.uid ?: return false

        val beforeGoals = dao.getTodayAllGoals(today)
        val unfinishedBeforeIds = beforeGoals.filter { !it.isDone }.map { it.id }.toSet()
        val unfinishedBeforeRemoteIds = beforeGoals.filter { !it.isDone }.map { it.remoteId }.toSet()

        dao.incrementStepsForActiveGoals(stepsDelta, today)
        dao.checkAndMarkCompletedGoals()

        val afterGoals = dao.getTodayAllGoals(today)

        afterGoals.forEach { goal ->
            if (goal.isDone && unfinishedBeforeIds.contains(goal.id)) {
                if (!notifiedGoalIds.contains(goal.id)) {
                    try {
                        showAchieveNotification(context) // 알림 발생!
                        notifiedGoalIds.add(goal.id)
                        Log.d("ALARM_SUCCESS", "🎉 알림 전송 성공: ${goal.title}")
                    } catch (e: Exception) {
                        Log.e("ALARM_ERROR", "알림 띄우기 실패: ${e.message}")
                    }
                }
            }
        }

        val newlyCompletedGoals = afterGoals.filter { it.isDone && unfinishedBeforeRemoteIds.contains(it.remoteId) }

        if (newlyCompletedGoals.isNotEmpty()) {
            updateStatsChallengeCount(uid, newlyCompletedGoals.size)
        }

        return newlyCompletedGoals.isNotEmpty()
    }

    suspend fun syncActiveGoalsToFirebase() {
        val uid = auth.currentUser?.uid ?: return
        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        val currentGoals = dao.getTodayAllGoals(today)

        currentGoals.forEach { goal ->
            updateGoalProgressOnFirebase(uid, goal)
        }
    }

    suspend fun updateGoalProgressOnFirebase(uid: String, goal: GoalEntity) {
        if (goal.remoteId.isEmpty()) return

        try {
            val updates = hashMapOf(
                "remoteId" to goal.remoteId,
                "title" to goal.title,
                "targetSteps" to goal.targetSteps,
                "currentSteps" to goal.currentSteps,
                "startDate" to goal.startDate,
                "endDate" to goal.endDate,
                "isDone" to goal.isDone,
                "updatedAt" to Timestamp.now()
            )
            col(uid).document(goal.remoteId).set(updates, SetOptions.merge()).await()
        } catch (e: Exception) {
            Log.e("FIREBASE_SYNC", "목표 진행률 저장 실패: ${e.message}")
        }
    }

    private suspend fun updateStatsChallengeCount(uid: String, incrementValue: Int) {
        try {
            val statsDoc = db.collection("users").document(uid).collection("stats").document("challenge")
            val data = mapOf(
                "total" to FieldValue.increment(incrementValue.toLong()),
                "date" to Timestamp.now()
            )
            statsDoc.set(data, SetOptions.merge()).await()
            Log.d("GoalRepo", "✅ 누적 완수 개수 $incrementValue 증가 완료")
        } catch (e: Exception) {
            Log.e("GoalRepo", "❌ 누적 개수 반영 실패: ${e.message}")
        }
    }
}