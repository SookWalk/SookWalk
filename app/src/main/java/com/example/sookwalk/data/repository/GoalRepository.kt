package com.example.sookwalk.data.repository

import com.example.sookwalk.data.local.dao.GoalDao
import com.example.sookwalk.data.local.entity.goal.GoalEntity
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class GoalRepository @Inject constructor(
    private val dao: GoalDao
){
    // 기본 CRUD
    suspend fun insertGoal(goal: GoalEntity): Long {
        return dao.insert(goal)
    }

    suspend fun deleteGoal(goal: GoalEntity) = dao.delete(goal)

    suspend fun updateGoal(goal: GoalEntity) = dao.update(goal)

    fun getGoalsByDate(date: String): Flow<List<GoalEntity>> = dao.getGoalsByDate(date)

    fun getGoalsOfWeek(weekStart: String, weekEnd: String): Flow<List<GoalEntity>> =
        dao.getGoalsOfWeek(weekStart, weekEnd)

    suspend fun getAllGoalsOnce(): List<GoalEntity> {
        return dao.getAllGoalsOnce()
    }

    suspend fun updateGoalByMemo(goalId: Int, memo: String) {
        dao.updateMemo(goalId, memo)
    }

}