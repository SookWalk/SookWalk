package com.example.sookwalk.data.local.entity.steps

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "steps")
data class DailyStepEntity(
    @PrimaryKey val date: String,
    val steps: Int
)


