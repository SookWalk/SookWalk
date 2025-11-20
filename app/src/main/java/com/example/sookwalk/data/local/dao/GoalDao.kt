package com.example.sookwalk.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.sookwalk.data.local.entity.goal.GoalEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface GoalDao {
    // 기본 CRUD
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goalEntity: GoalEntity): Long

    @Update
    suspend fun update(goal: GoalEntity)

    @Delete
    suspend fun delete(goal: GoalEntity)

    // 기한 안에 유효한 목표 조회
    @Query("""
        SELECT * FROM goals
        WHERE :date BETWEEN startDate AND endDate
        ORDER BY endDate ASC
    """)
    fun getGoalsByDate(date: String): Flow<List<GoalEntity>>

    @Query("""
        SELECT * FROM goals
        WHERE :weekStart <= endDate AND :weekEnd >= startDate
        ORDER BY endDate ASC
    """)
    fun getGoalsOfWeek(
        weekStart: String,
        weekEnd: String
    ): Flow<List<GoalEntity>>


    // 알람 설정/취소를 위한 goal 조회
    // Flow로 observe하는 것이 아니라 단순히 전체 리스트 반환
    @Query("SELECT * FROM goals")
    suspend fun getAllGoalsOnce(): List<GoalEntity>

    // 메모만 수정
    @Query("UPDATE goals SET memo = :memo WHERE id = :goalId")
    suspend fun updateMemo(goalId: Int, memo: String)

    // 걸음 수 들어올 때마다 업데이트
    @Query("UPDATE goals SET currentSteps = :steps WHERE id = :goalId")
    suspend fun updateGoalSteps(goalId: Int, steps: Int)

    // 목표 달성
    @Query("UPDATE goals SET isDone = 1 WHERE id = :goalId")
    suspend fun markGoalCompleted(goalId: Int)

//    fun getCompletedTodos(): Flow<List<GoalEntity>>
}