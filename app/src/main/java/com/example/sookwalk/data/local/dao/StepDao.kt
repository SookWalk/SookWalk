package com.example.sookwalk.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.sookwalk.data.local.entity.steps.DailyStepEntity

@Dao
interface StepDao {

    @Query("SELECT steps FROM steps WHERE date = :date LIMIT 1")
    suspend fun getSteps(date: String): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSteps(step: DailyStepEntity)

    @Query("SELECT SUM(steps) FROM steps WHERE date >= :start AND date <= :end")
    suspend fun getStepsBetween(start: String, end: String): Int
}
