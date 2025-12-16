package com.example.sookwalk.data.repository

import com.example.sookwalk.data.local.dao.GoalDao
import com.example.sookwalk.data.local.entity.goal.GoalEntity
import com.example.sookwalk.data.remote.dto.GoalDto
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GoalRepository @Inject constructor(
    private val dao: GoalDao,
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
){
    private fun col(uid: String) =
        db.collection("users").document(uid).collection("goals")

    suspend fun insertGoal(uid: String, goal: GoalEntity): Long {
        val newDocRef = col(uid).document()
        val remoteId = newDocRef.id

        val goalWithRemote = goal.copy(remoteId = remoteId)
        val localId = dao.insert(goalWithRemote)

        CoroutineScope(Dispatchers.IO).launch {
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
        }

        return localId
    }

    suspend fun deleteGoal(uid: String, goal: GoalEntity){
        // Room 삭제
        dao.delete(goal)

        // Firestore 삭제 (remoteId가 있을 때만)
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

                    val localGoal = dao.getGoalByRemoteId(remoteGoal.remoteId) // ★ DAO 필요 함수

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
            android.util.Log.e("GoalRepository", "Goals sync failed: ${e.message}")
            e.printStackTrace()
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
        // Room 업데이트
        dao.updateMemo(goal.id, memo)

        // Firestore 업데이트
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

    suspend fun updateActiveGoalsProgress(stepsDelta: Int) {
        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        dao.incrementStepsForActiveGoals(stepsDelta, today)
        dao.checkAndMarkCompletedGoals()

        val uid = auth.currentUser?.uid // Firebase auth는 Repository에 주입받아야 합니다.
        if (uid == null) return

        val updatedGoals = dao.getActiveGoalsOnce(today) // Room에서 업데이트된 목표 목록 가져오기

        CoroutineScope(Dispatchers.IO).launch {
            updatedGoals.forEach { goal ->
                updateGoalProgressOnFirebase(uid, goal)
            }
        }
    }

    suspend fun updateGoalProgressOnFirebase(uid: String, goal: GoalEntity) {
        if (goal.remoteId.isEmpty()) return // remoteId가 없으면 Firebase에 저장할 수 없음

        try {
            val updates = mapOf(
                "currentSteps" to goal.currentSteps,
                "isDone" to goal.isDone,
                "updatedAt" to Timestamp.now()
            )
            col(uid).document(goal.remoteId).update(updates).await()
        } catch (e: Exception) {
            android.util.Log.e("FIREBASE_SYNC", "목표 진행률 업데이트 실패: ${e.message}")
            e.printStackTrace()
        }
    }
}