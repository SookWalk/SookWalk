package com.example.sookwalk.data.local.entity.goal

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "title")
    val title: String,

    // 목표 걸음 수
    val targetSteps: Int,
    // 현재까지 채운 걸음 수
    val currentSteps: Int,

    // yyyy-MM-dd
    @ColumnInfo(name = "startDate")
    val startDate: String,

    // 목표 기한
    @ColumnInfo(name = "endDate")
    val endDate: String,

    @ColumnInfo(name = "memo")
    val memo: String = "",

    @ColumnInfo(name = "isDone")
    val isDone: Boolean = false,
    )