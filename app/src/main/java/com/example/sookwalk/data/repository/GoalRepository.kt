package com.example.sookwalk.data.repository

import com.example.sookwalk.data.local.dao.GoalDao
import com.example.sookwalk.data.local.entity.goal.GoalEntity
import com.example.sookwalk.data.remote.dto.GoalDto
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

class GoalRepository @Inject constructor(
    private val dao: GoalDao,
    private val db: FirebaseFirestore = Firebase.firestore
){
    private fun col(uid: String) =
        db.collection("users").document(uid).collection("goals")

    // 기본 CRUD
    suspend fun insertGoal(uid: String,goal: GoalEntity): String {
        val dto = GoalDto(goal.id.toString(), goal.title, goal.targetSteps, goal.currentSteps, goal.startDate, goal.endDate, goal.memo, goal.isDone)
        val ref = col(uid).add(dto).await()
        dao.insert(goal)
        return ref.id
    }

    suspend fun deleteGoal(uid: String, id:String, goal: GoalEntity){
        col(uid).document(id).delete().await()
        dao.delete(goal)
    }

    suspend fun updateGoal(goal: GoalEntity) = dao.update(goal)

    fun getGoalsByDate(date: String): Flow<List<GoalEntity>> = dao.getGoalsByDate(date)

    fun getGoalsOfWeek(weekStart: String, weekEnd: String): Flow<List<GoalEntity>> =
        dao.getGoalsOfWeek(weekStart, weekEnd)

    suspend fun getAllGoalsOnce(): List<GoalEntity> {
        return dao.getAllGoalsOnce()
    }

    suspend fun updateGoalByMemo(uid: String, goalId: Int, memo: String) {
        val m = mutableMapOf<String, Any>(
            "updatedAt" to Timestamp.now()
        )
        m["memo"] = memo
        col(uid).document(goalId.toString()).update(m).await()
        dao.updateMemo(goalId, memo)
    }
}